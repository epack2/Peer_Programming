package service;

import exception.EnrollmentException;
import exception.ValidationException;
import model.Course;
import model.Student;
import repo.CourseRepository;
import repo.EnrollmentRepository;
import repo.StudentRepository;
import util.LoggerUtil;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Business rules live here: add/list/search, enroll/drop logic, waitlist FIFO promotion.
 * This class is designed for easy testing (no UI, no file IO directly).
 */
public class RegistrationService {
    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private static final Logger log = LoggerUtil.getLogger(RegistrationService.class);

    public RegistrationService(StudentRepository sr, CourseRepository cr, EnrollmentRepository er) {
        this.studentRepo = sr;
        this.courseRepo = cr;
        this.enrollmentRepo = er;
    }

    public void loadAll() {
        studentRepo.ensureLoaded();
        courseRepo.ensureLoaded();
        try {
            enrollmentRepo.loadEnrollments(courseRepo.findAll());
        } catch (Exception e) {
            log.severe("Failed to load enrollments: " + e.getMessage());
        }
    }

    public void saveAll() {
        try {
            studentRepo.saveAll(studentRepo.findAll());
            courseRepo.saveAll(courseRepo.findAll());
            enrollmentRepo.saveEnrollments(courseRepo.findAll());
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
    public void addStudent(String id, String name, String email) {
        ValidationUtil.requireNonEmpty(id, "Banner ID");
        ValidationUtil.requireNonEmpty(name, "Name");
        ValidationUtil.validateBannerId(id);
        ValidationUtil.validateEmail(email);

        // duplicate check
        Optional<Student> existing = studentRepo.findById(id);
        if (existing.isPresent()) {
            throw new ValidationException("Student with id already exists: " + id);
        }
        Student s = new Student(id, name, email);
        try {
            studentRepo.save(s);
            log.info("ADD_STUDENT " + id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save student: " + e.getMessage(), e);
        }
    }

    public List<Student> listStudents() {
        return studentRepo.findAll();
    }

    public List<Student> searchStudents(String q) {
        return studentRepo.search(q == null ? "" : q);
    }

    // --- Course operations ---
    public void addCourse(String code, String title, int capacity) {
        ValidationUtil.requireNonEmpty(code, "Course code");
        ValidationUtil.requireNonEmpty(title, "Course title");
        ValidationUtil.requireRange(capacity, 1, 500, "Capacity");

        Optional<Course> existing = courseRepo.findByCode(code);
        if (existing.isPresent()) throw new ValidationException("Course already exists: " + code);

        Course c = new Course(code, title, capacity);
        try {
            courseRepo.save(c);
            log.info("ADD_COURSE " + code);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save course: " + e.getMessage(), e);
        }
    }

    public List<Course> listCourses() {
        return courseRepo.findAll();
    }

    public List<Course> searchCourses(String q) {
        return courseRepo.search(q == null ? "" : q);
    }

    // --- Enrollment logic (business rules) ---
    /**
     * Enroll a student into a course. Returns true if enrolled in roster; false if waitlisted.
     */
    public boolean enroll(String studentId, String courseCode) {
        ValidationUtil.requireNonEmpty(studentId, "Student ID");
        ValidationUtil.requireNonEmpty(courseCode, "Course code");

        // student must exist
        Optional<Student> st = studentRepo.findById(studentId);
        if (!st.isPresent()) throw new ValidationException("Unknown student: " + studentId);

        // course must exist
        Optional<Course> co = courseRepo.findByCode(courseCode);
        if (!co.isPresent()) throw new ValidationException("Unknown course: " + courseCode);

        Course c = co.get();

        // duplicates
        if (c.rosterContains(studentId)) throw new EnrollmentException("Student already enrolled in course");
        if (c.waitlistContains(studentId)) throw new EnrollmentException("Student already waitlisted for course");

        boolean enrolled;
        if (c.isFull()) {
            c.addToWaitlist(studentId);
            enrolled = false;
            log.info("WAITLIST " + studentId + " -> " + courseCode);
        } else {
            c.addToRoster(studentId);
            enrolled = true;
            log.info("ENROLL " + studentId + " -> " + courseCode);
        }

        persistCourse(c);
        return enrolled;
    }

    /**
     * Drop student from roster or waitlist. Returns true if a waitlisted student was promoted.
     */
    public boolean drop(String studentId, String courseCode) {
        ValidationUtil.requireNonEmpty(studentId, "Student ID");
        ValidationUtil.requireNonEmpty(courseCode, "Course code");

        Optional<Course> co = courseRepo.findByCode(courseCode);
        if (!co.isPresent()) throw new ValidationException("Unknown course: " + courseCode);
        Course c = co.get();

        boolean promoted = false;
        if (c.removeFromRoster(studentId)) {
            log.info("DROP " + studentId + " from " + courseCode);
            String promotedId = c.promoteOneFromWaitlist();
            if (promotedId != null) {
                log.info("PROMOTE " + promotedId + " -> " + courseCode);
                promoted = true;
            }
        } else if (c.removeFromWaitlist(studentId)) {
            log.info("WAITLIST_REMOVE " + studentId + " " + courseCode);
        } else {
            throw new EnrollmentException("Not enrolled or waitlisted");
        }

        persistCourse(c);
        return promoted;
    }

    private void persistCourse(Course c) {
        try {
            courseRepo.save(c);
            // save enrollments (entire set)
            enrollmentRepo.saveEnrollments(courseRepo.findAll());
        } catch (Exception e) {
            log.severe("Failed to persist course/enrollments: " + e.getMessage());
            throw new RuntimeException("Storage error: " + e.getMessage(), e);
        }
    }
}
