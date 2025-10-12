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
        boolean demo = args.length > 0 && "--demo".equalsIgnoreCase(args[0]);

        // Repos (CSV file paths from config / system props)
        String studentsFile = Config.get("students.file", "students.csv");
        String coursesFile = Config.get("courses.file", "courses.csv");
        String enrollmentsFile = Config.get("enrollments.file", "enrollments.csv");

        StudentRepository studentRepo = new CsvStudentRepository(studentsFile);
        CourseRepository courseRepo = new CsvCourseRepository(coursesFile);
        EnrollmentRepository enrollmentRepo = new CsvEnrollmentRepository(enrollmentsFile);

        RegistrationService service = new RegistrationService(studentRepo, courseRepo, enrollmentRepo);

        if (demo) {
            service.seedDemoData();
            log.info("Seeded demo data");
        } else {
            service.loadAll();
        }

        println("=== UCA Course Registration (Refactored) ===");
        println("Files: students=" + studentsFile + " courses=" + coursesFile + " enrollments=" + enrollmentsFile);
        menuLoop(service);
        service.saveAll();
        println("Goodbye!");
    }

    private static void menuLoop(RegistrationService service) {
        Scanner sc = new Scanner(System.in);
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
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        addStudentUI(sc, service);
                        break;
                    case "2":
                        addCourseUI(sc, service);
                        break;
                    case "3":
                        enrollUI(sc, service);
                        break;
                    case "4":
                        dropUI(sc, service);
                        break;
                    case "5":
                        listStudents(service);
                        break;
                    case "6":
                        listCourses(service);
                        break;
                    case "7":
                        searchStudentUI(sc, service);
                        break;
                    case "8":
                        searchCourseUI(sc, service);
                        break;
                    case "0":
                        return;
                    default:
                        println("Invalid");
                }
            } catch (ValidationException | EnrollmentException e) {
                println("ERROR: " + e.getMessage());
            } catch (Exception e) {
                // Safeguard: don't crash the UI
                println("Unexpected error: " + e.getMessage());
                LoggerUtil.getLogger(MainCLI.class).severe("Unexpected: " + e.getMessage());
            }
        }
    }

    private static void addStudentUI(Scanner sc, RegistrationService service) {
        print("Banner ID: ");
        String id = sc.nextLine().trim();
        print("Name: ");
        String name = sc.nextLine().trim();
        print("Email: ");
        String email = sc.nextLine().trim();
        service.addStudent(id, name, email);
        println("Student added.");
    }

    private static void addCourseUI(Scanner sc, RegistrationService service) {
        print("Course Code: ");
        String code = sc.nextLine().trim();
        print("Title: ");
        String title = sc.nextLine().trim();
        print("Capacity (1-500): ");
        String capStr = sc.nextLine().trim();
        int cap = Integer.parseInt(capStr);
        service.addCourse(code, title, cap);
        println("Course added.");
    }

    private static void enrollUI(Scanner sc, RegistrationService service) {
        print("Student ID: ");
        String sid = sc.nextLine().trim();
        print("Course Code: ");
        String cc = sc.nextLine().trim();
        boolean enrolled = service.enroll(sid, cc);
        if (enrolled) println("Enrolled.");
        else println("Course full. Added to waitlist.");
    }

    private static void dropUI(Scanner sc, RegistrationService service) {
        print("Student ID: ");
        String sid = sc.nextLine().trim();
        print("Course Code: ");
        String cc = sc.nextLine().trim();
        boolean promoted = service.drop(sid, cc);
        if (promoted) println("Dropped and a waitlisted student was promoted.");
        else println("Dropped.");
    }

    private static void listStudents(RegistrationService service) {
        println("Students:");
        List<Student> students = service.listStudents();
        for (Student s : students) println(" - " + s);
    }

    private static void listCourses(RegistrationService service) {
        println("Courses:");
        List<Course> courses = service.listCourses();
        for (Course c : courses) {
            println(" - " + c.getCode() + " " + c.getTitle() + " cap=" + c.getCapacity()
                    + " enrolled=" + c.getRoster().size() + " wait=" + c.getWaitlist().size());
        }
    }

    private static void searchStudentUI(Scanner sc, RegistrationService service) {
        print("Query (id or name substring): ");
        String q = sc.nextLine().trim();
        List<Student> results = service.searchStudents(q);
        if (results.isEmpty()) println("No students found.");
        else {
            println("Found:");
            for (Student s : results) println(" - " + s);
        }
    }

    private static void searchCourseUI(Scanner sc, RegistrationService service) {
        print("Query (code or title substring): ");
        String q = sc.nextLine().trim();
        List<Course> results = service.searchCourses(q);
        if (results.isEmpty()) println("No courses found.");
        else {
            println("Found:");
            for (Course c : results) println(" - " + c.getCode() + " " + c.getTitle());
        }
    }

    private static void print(String s) { System.out.print(s); }
    private static void println(String s) { System.out.println(s); }
}
