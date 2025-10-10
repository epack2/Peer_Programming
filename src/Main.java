//import repo.*;
//import service.*;
//import util.Log;
//
//import java.io.IOException;
//import java.util.Scanner;
//
//public class Main {
//    public static void main(String[] args) {
//        try {
//            // Instantiate repositories
//            StudentRepository studentRepo = new CsvStudentRepository();
//            CourseRepository courseRepo = new CsvCourseRepository();
//            EnrollmentRepository enrollmentRepo = new CsvEnrollmentRepository();
//
//            // Create the service
//            RegistrationService service = new RegistrationService(studentRepo, courseRepo, enrollmentRepo);
//            service.importCsvOnce();
//
//            // Simple text-based interface
//            Scanner scanner = new Scanner(System.in);
//            boolean running = true;
//
//            System.out.println("===== University Registration System =====");
//
//            while (running) {
//                System.out.println("\n1. Add Student");
//                System.out.println("2. Add Course");
//                System.out.println("3. Enroll Student");
//                System.out.println("4. Drop Student");
//                System.out.println("5. List Students");
//                System.out.println("6. List Courses");
//                System.out.println("0. Exit");
//                System.out.print("Select option: ");
//                int choice = Integer.parseInt(scanner.nextLine());
//
//                switch (choice) {
//                    case 1 -> {
//                        System.out.print("Student ID: ");
//                        String id = scanner.nextLine();
//                        System.out.print("Name: ");
//                        String name = scanner.nextLine();
//                        System.out.print("Email: ");
//                        String email = scanner.nextLine();
//                        service.addStudent(id, name, email);
//                    }
//                    case 2 -> {
//                        System.out.print("Course Code: ");
//                        String code = scanner.nextLine();
//                        System.out.print("Title: ");
//                        String title = scanner.nextLine();
//                        System.out.print("Capacity: ");
//                        int cap = Integer.parseInt(scanner.nextLine());
//                        service.addCourse(code, title, cap);
//                    }
//                    case 3 -> {
//                        System.out.print("Student ID: ");
//                        String sid = scanner.nextLine();
//                        System.out.print("Course Code: ");
//                        String ccode = scanner.nextLine();
//                        service.enroll(sid, ccode);
//                    }
//                    case 4 -> {
//                        System.out.print("Student ID: ");
//                        String sid = scanner.nextLine();
//                        System.out.print("Course Code: ");
//                        String ccode = scanner.nextLine();
//                        service.drop(sid, ccode);
//                    }
//                    case 5 -> service.listStudents().forEach(System.out::println);
//                    case 6 -> service.listCourses().forEach(System.out::println);
//                    case 0 -> {
//                        running = false;
//                        service.close();
//                        System.out.println("Goodbye!");
//                    }
//                    default -> System.out.println("Invalid option.");
//                }
//            }
//
//        } catch (IOException e) {
//            Log.error("I/O Error: " + e.getMessage());
//        } catch (ValidationException | EnrollmentException e) {
//            Log.error("Validation error: " + e.getMessage());
//        } catch (Exception e) {
//            Log.error("Unexpected error: " + e.getMessage());
//        }
//    }
//}
