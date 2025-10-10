package service;

import model.Course;
import model.Student;
import repo.CourseRepository;
import repo.EnrollmentRepository;
import repo.StudentRepository;
import util.Validation;
import util.Log;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class RegistrationService {

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;

    public RegistrationService(StudentRepository studentRepo, CourseRepository courseRepo, EnrollmentRepository enrollmentRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
        this.enrollmentRepo = enrollmentRepo;
    }

    // ----------------- Initialization -----------------
    public void importCsvOnce() throws IOException {
        studentRepo.importCsvOnce();
        courseRepo.importCsvOnce();
        enrollmentRepo.importCsvOnce();
    }

    // ----------------- Student Operations -----------------
    public void addStudent(String id, String name, String email) throws ValidationException, IOException {
        Validation.requireNotBlank(id, "Student ID");
        Validation.requireNotBlank(name, "Name");
        Validation.requireNotBlank(email, "Email");
        Validation.validateBanner(id);
        Validation.validateEmail(email);

        if (studentRepo.existsById(id))
            throw new ValidationException("Student with ID already exists: " + id);

        studentRepo.save(new Student(id, name, email));
        Log.info("Added student: " + id);
    }

    public Collection<Student> listStudents() throws IOException {
        return studentRepo.findAll();
    }

    public Optional<Student> findStudent(String id) throws IOException {
        return studentRepo.findById(id);
    }

    // ----------------- Course Operations -----------------
    public void addCourse(String code, String title, int capacity) throws ValidationException, IOException {
        Validation.requireNotBlank(code, "Course code");
        Validation.requireNotBlank(title, "Course title");
        Validation.validateCapacity(capacity);

        if (courseRepo.existsByCode(code))
            throw new ValidationException("Course already exists: " + code);

        courseRepo.save(new Course(code, title, capacity));
        Log.info("Added course: " + code);
    }

    public Collection<Course> listCourses() throws IOException {
        return loadCoursesWithEnrollments();
    }

    public Optional<Course> findCourse(String code) throws IOException {
        return courseRepo.findByCode(code);
    }

    // ----------------- Enrollment Operations -----------------
    public void enroll(String studentId, String courseCode) throws EnrollmentException, IOException {
        Validation.requireNotBlank(studentId, "Student ID");
        Validation.requireNotBlank(courseCode, "Course code");

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new EnrollmentException("Unknown student: " + studentId));

        Course course = courseRepo.findByCode(courseCode)
                .orElseThrow(() -> new EnrollmentException("Unknown course: " + courseCode));

        if (course.rosterContains(studentId))
            throw new EnrollmentException("Student already enrolled in course: " + courseCode);
        if (course.waitlistContains(studentId))
            throw new EnrollmentException("Student already waitlisted for course: " + courseCode);

        if (course.rosterSize() >= course.getCapacity()) {
            course.addToWaitlist(studentId);
            Log.info("Waitlisted " + studentId + " -> " + courseCode);
        } else {
            course.addToRoster(studentId);
            Log.info("Enrolled " + studentId + " -> " + courseCode);
        }

        courseRepo.save(course);
        enrollmentRepo.saveAll(loadCoursesWithEnrollments());
    }

    public void drop(String studentId, String courseCode) throws EnrollmentException, IOException {
        Validation.requireNotBlank(studentId, "Student ID");
        Validation.requireNotBlank(courseCode, "Course code");

        Course course = courseRepo.findByCode(courseCode)
                .orElseThrow(() -> new EnrollmentException("Unknown course: " + courseCode));

        boolean removedFromRoster = course.removeFromRoster(studentId);
        boolean removedFromWait = course.removeFromWaitlist(studentId);

        if (!removedFromRoster && !removedFromWait)
            throw new EnrollmentException("Student not enrolled or waitlisted for " + courseCode);

        // Promote first from waitlist
        if (removedFromRoster) {
            String promote = course.pollWaitlist();
            if (promote != null) {
                course.addToRoster(promote);
                Log.info("Promoted " + promote + " -> " + courseCode);
            }
        }

        courseRepo.save(course);
        enrollmentRepo.saveAll(loadCoursesWithEnrollments());
        Log.info("Dropped " + studentId + " from " + courseCode);
    }

    // ----------------- Helpers -----------------
    private Collection<Course> loadCoursesWithEnrollments() throws IOException {
        Collection<Course> courses = courseRepo.findAll();
        enrollmentRepo.loadEnrollmentsInto(courses);
        return courses;
    }

    public void saveAll() throws IOException {
        enrollmentRepo.saveAll(courseRepo.findAll());
    }

    public void close() throws IOException {
        studentRepo.close();
        courseRepo.close();
        enrollmentRepo.close();
    }
}
