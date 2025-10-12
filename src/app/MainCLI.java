package app;

import model.*;
import repo.*;
import repo.impl.*;
import service.RegistrationService;
import util.*;
import exception.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * CLI entry point. Keeps UI only: parsing user input and delegating to service.
 */
public class MainCLI {
    private static final Logger log = LoggerUtil.getLogger(MainCLI.class);

    public static void main(String[] args) {
        boolean demoMode = args.length > 0 && "--demo".equalsIgnoreCase(args[0]);

        // File paths from config / system properties
        String studentsFilePath = Config.get("students.file", "students.csv");
        String coursesFilePath = Config.get("courses.file", "courses.csv");
        String enrollmentsFilePath = Config.get("enrollments.file", "enrollments.csv");

        StudentRepository studentRepository = new CsvStudentRepository(studentsFilePath);
        CourseRepository courseRepository = new CsvCourseRepository(coursesFilePath);
        EnrollmentRepository enrollmentRepository = new CsvEnrollmentRepository(enrollmentsFilePath);

        RegistrationService registrationService = new RegistrationService(
                studentRepository, courseRepository, enrollmentRepository
        );

        if (demoMode) {
            registrationService.seedDemoData();
            log.info("Seeded demo data");
        } else {
            registrationService.loadAll();
        }

        println("=== UCA Course Registration (Refactored) ===");
        println("Files: students=" + studentsFilePath
                + " courses=" + coursesFilePath
                + " enrollments=" + enrollmentsFilePath);

        runMenuLoop(registrationService);
        registrationService.saveAll();
        println("Goodbye!");
    }

    private static void runMenuLoop(RegistrationService registrationService) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            println("\nMenu:");
            println("1) Add student");
            println("2) Add course");
            println("3) Enroll student in course");
            println("4) Drop student from course");
            println("5) List students");
            println("6) List courses");
            println("7) Search student");
            println("8) Search course");
            println("0) Exit");
            print("Choose: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> addStudentUI(scanner, registrationService);
                    case "2" -> addCourseUI(scanner, registrationService);
                    case "3" -> enrollStudentUI(scanner, registrationService);
                    case "4" -> dropStudentUI(scanner, registrationService);
                    case "5" -> displayStudents(registrationService);
                    case "6" -> displayCourses(registrationService);
                    case "7" -> searchStudentUI(scanner, registrationService);
                    case "8" -> searchCourseUI(scanner, registrationService);
                    case "0" -> { return; }
                    default -> println("Invalid choice.");
                }
            } catch (ValidationException | EnrollmentException e) {
                println("ERROR: " + e.getMessage());
            } catch (Exception e) {
                println("Unexpected error: " + e.getMessage());
                log.severe("Unexpected: " + e.getMessage());
            }
        }
    }

    private static void addStudentUI(Scanner scanner, RegistrationService registrationService) {
        print("Banner ID: ");
        String bannerId = scanner.nextLine().trim();
        print("Name: ");
        String name = scanner.nextLine().trim();
        print("Email: ");
        String email = scanner.nextLine().trim();
        registrationService.addStudent(bannerId, name, email);
        println("Student added.");
    }

    private static void addCourseUI(Scanner scanner, RegistrationService registrationService) {
        print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        print("Title: ");
        String title = scanner.nextLine().trim();
        print("Capacity (1-500): ");
        int capacity = Integer.parseInt(scanner.nextLine().trim());
        registrationService.addCourse(courseCode, title, capacity);
        println("Course added.");
    }

    private static void enrollStudentUI(Scanner scanner, RegistrationService registrationService) {
        print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        boolean enrolled = registrationService.enroll(studentId, courseCode);
        println(enrolled ? "Enrolled." : "Course full. Added to waitlist.");
    }

    private static void dropStudentUI(Scanner scanner, RegistrationService registrationService) {
        print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        boolean promoted = registrationService.drop(studentId, courseCode);
        println(promoted ? "Dropped and a waitlisted student was promoted." : "Dropped.");
    }

    private static void displayStudents(RegistrationService registrationService) {
        println("Students:");
        List<Student> allStudents = registrationService.listStudents();
        for (Student student : allStudents) {
            println(" - " + student);
        }
    }

    private static void displayCourses(RegistrationService registrationService) {
        println("Courses:");
        List<Course> allCourses = registrationService.listCourses();
        for (Course course : allCourses) {
            println(" - " + course.getCode() + " " + course.getTitle()
                    + " cap=" + course.getCapacity()
                    + " enrolled=" + course.getRoster().size()
                    + " wait=" + course.getWaitlist().size());
        }
    }

    private static void searchStudentUI(Scanner scanner, RegistrationService registrationService) {
        print("Query (id or name substring): ");
        String query = scanner.nextLine().trim();
        List<Student> results = registrationService.searchStudents(query);
        if (results.isEmpty()) println("No students found.");
        else {
            println("Found:");
            for (Student student : results) println(" - " + student);
        }
    }

    private static void searchCourseUI(Scanner scanner, RegistrationService registrationService) {
        print("Query (code or title substring): ");
        String query = scanner.nextLine().trim();
        List<Course> results = registrationService.searchCourses(query);
        if (results.isEmpty()) println("No courses found.");
        else {
            println("Found:");
            for (Course course : results) println(" - " + course.getCode() + " " + course.getTitle());
        }
    }

    private static void print(String s) { System.out.print(s); }
    private static void println(String s) { System.out.println(s); }
}
