import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.IO.print;
import static java.lang.IO.println;

public class EnrollmentRepository {
    static CourseRepository cRepo = new CourseRepository();
    static StudentRepository sRepo = new StudentRepository();

    private static void loadEnrollments() {
        File f = new File("enrollments.csv");
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Format: courseCode|studentId|ENROLLED or WAITLIST
                String[] p = line.split("\\|", -1);
                if (p.length >= 3) {
                    int courseCode = Integer.parseInt(p[0]);
                    int studentId = Integer.parseInt(p[1]);
                    String status = p[2];
                    Course c = cRepo.getCourseById(courseCode);
                    Student s = sRepo.getStudentById(studentId);
                    if (c == null) continue;
                    if ("ENROLLED".equalsIgnoreCase(status)) {
                        if (!c.roster.contains(studentId)) c.roster.add(studentId);
                    } else if ("WAITLIST".equalsIgnoreCase(status)) {
                        if (!c.waitlist.contains(studentId)) c.waitlist.add(studentId);
                    }
                }
            }
            Main.audit("LOAD enrollments");
        } catch (Exception e) {
            println("Failed load enrollments: " + e.getMessage());
        }
    }


    private static void enrollUI(Scanner sc) {
        print("Student ID: ");
        String sid = sc.nextLine().trim();
        print("Course Code: ");
        String cc = sc.nextLine().trim();
        Course c = cRepo.getCourseById(Integer.parseInt(cc));
        if (c == null) { println("No such course"); return; }
        if (c.roster.contains(Integer.parseInt(sid))) { println("Already enrolled"); return; }
        if (c.waitlist.contains(Integer.parseInt(sid))) { println("Already waitlisted"); return; }

        if (c.roster.size() >= c.capacity) {
            c.waitlist.add(Integer.parseInt(sid));
            Main.audit("WAITLIST " + sid + "->" + cc);
            println("Course full. Added to WAITLIST.");
        } else {
            c.roster.add(Integer.parseInt(sid));
            Main.audit("ENROLL " + sid + "->" + cc);
            println("Enrolled.");
        }
    }
}
