package service;

import exception.*;
import model.*;
import repo.*;
import util.*;
import java.util.*;
import java.util.logging.Logger;

/**
FIFO AND DECLARATIONS
 */
public class RegistrationService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private static final Logger log = LoggerUtil.getLogger(RegistrationService.class);

    public RegistrationService(StudentRepository studentRepository,
                               CourseRepository courseRepository,
                               EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public void loadAll() {
        studentRepository.ensureLoaded();
        courseRepository.ensureLoaded();
        try {
            enrollmentRepository.loadEnrollments(courseRepository.findAll());
        } catch (Exception e) {
            log.severe("Failed to load enrollments: " + e.getMessage());
        }
    }

    public void saveAll() {
        try {
            studentRepository.saveAll(studentRepository.findAll());
            courseRepository.saveAll(courseRepository.findAll());
            enrollmentRepository.saveEnrollments(courseRepository.findAll());
        } catch (Exception e) {
            log.severe("Failed to save data: " + e.getMessage());
        }
    }

    public void seedDemoData() {
        try {
            addStudent("B001", "Alice", "alice@uca.edu");
            addStudent("B002", "Brian", "brian@uca.edu");
            addCourse("CSCI4490", "Software Engineering", 2);
            addCourse("MATH1496", "Calculus I", 50);
            // reload so enrollments can be used later if demo enrollments added
            loadAll();
        } catch (Exception e) {
            log.warning("Demo seed partial: " + e.getMessage());
        }
    }

    // --- Student operations ---
    public void addStudent(String bannerId, String name, String email) {
        ValidationUtil.requireNonEmpty(bannerId, "Banner ID");
        ValidationUtil.requireNonEmpty(name, "Name");
        ValidationUtil.validateBannerId(bannerId);
        ValidationUtil.validateEmail(email);

        // duplicate check
        Optional<Student> existingStudent = studentRepository.findById(bannerId);
        if (existingStudent.isPresent()) {
            throw new ValidationException("Student with id already exists: " + bannerId);
        }
        Student student = new Student(bannerId, name, email);
        try {
            studentRepository.save(student);
            log.info("ADD_STUDENT " + bannerId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save student: " + e.getMessage(), e);
        }
    }

    public List<Student> listStudents() {
        return studentRepository.findAll();
    }

    public List<Student> searchStudents(String query) {
        return studentRepository.search(query == null ? "" : query);
    }

    // --- Course operations ---
    public void addCourse(String courseCode, String title, int capacity) {
        ValidationUtil.requireNonEmpty(courseCode, "Course code");
        ValidationUtil.requireNonEmpty(title, "Course title");
        ValidationUtil.requireRange(capacity, 1, 500, "Capacity");

        Optional<Course> existingCourse = courseRepository.findByCode(courseCode);
        if (existingCourse.isPresent()) throw new ValidationException("Course already exists: " + courseCode);

        Course course = new Course(courseCode, title, capacity);
        try {
            courseRepository.save(course);
            log.info("ADD_COURSE " + courseCode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save course: " + e.getMessage(), e);
        }
    }

    public List<Course> listCourses() {
        return courseRepository.findAll();
    }

    public List<Course> searchCourses(String query) {
        return courseRepository.search(query == null ? "" : query);
    }

    // --- Enrollment logic (business rules) ---
    /**
     * Enroll a student into a course. Returns true if enrolled in roster; false if waitlisted.
     */
    public boolean enroll(String studentId, String courseCode) {
        ValidationUtil.requireNonEmpty(studentId, "Student ID");
        ValidationUtil.requireNonEmpty(courseCode, "Course code");

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (!studentOpt.isPresent()) throw new ValidationException("Unknown student: " + studentId);

        Optional<Course> courseOpt = courseRepository.findByCode(courseCode);
        if (!courseOpt.isPresent()) throw new ValidationException("Unknown course: " + courseCode);

        Course course = courseOpt.get();

        if (course.rosterContains(studentId)) throw new EnrollmentException("Student already enrolled in course");
        if (course.waitlistContains(studentId)) throw new EnrollmentException("Student already waitlisted for course");

        boolean enrolled;
        if (course.isFull()) {
            course.addToWaitlist(studentId);
            enrolled = false;
            log.info("WAITLIST " + studentId + " -> " + courseCode);
        } else {
            course.addToRoster(studentId);
            enrolled = true;
            log.info("ENROLL " + studentId + " -> " + courseCode);
        }

        persistCourse(course);
        return enrolled;
    }

    /**
     * Drop student from roster or waitlist. Returns true if a waitlisted student was promoted.
     */
    public boolean drop(String studentId, String courseCode) {
        ValidationUtil.requireNonEmpty(studentId, "Student ID");
        ValidationUtil.requireNonEmpty(courseCode, "Course code");

        Optional<Course> courseOpt = courseRepository.findByCode(courseCode);
        if (!courseOpt.isPresent()) throw new ValidationException("Unknown course: " + courseCode);
        Course course = courseOpt.get();

        boolean promoted = false;
        if (course.removeFromRoster(studentId)) {
            log.info("DROP " + studentId + " from " + courseCode);
            String promotedStudentId = course.promoteOneFromWaitlist();
            if (promotedStudentId != null) {
                log.info("PROMOTE " + promotedStudentId + " -> " + courseCode);
                promoted = true;
            }
        } else if (course.removeFromWaitlist(studentId)) {
            log.info("WAITLIST_REMOVE " + studentId + " " + courseCode);
        } else {
            throw new EnrollmentException("Not enrolled or waitlisted");
        }

        persistCourse(course);
        return promoted;
    }

    private void persistCourse(Course course) {
        try {
            courseRepository.save(course);
            enrollmentRepository.saveEnrollments(courseRepository.findAll());
        } catch (Exception e) {
            log.severe("Failed to persist course/enrollments: " + e.getMessage());
            throw new RuntimeException("Storage error: " + e.getMessage(), e);
        }
    }
}
