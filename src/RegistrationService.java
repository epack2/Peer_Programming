import java.util.Scanner;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.IO.print;
import static java.lang.IO.println;

public class RegistrationService {
    static CourseRepository cRepo = new CourseRepository();
    static StudentRepository sRepo = new StudentRepository();


    private static void enrollUI(Scanner sc) {
        print("Student ID: ");
        int sid = Integer.parseInt(sc.nextLine());
        print("Course Code: ");
        int cc = Integer.parseInt(sc.nextLine());
        Course c = cRepo.getCourseById(cc);
        if (c == null) { println("No such course"); return; }
        if (c.roster.contains(sid)) { println("Already enrolled"); return; }
        if (c.waitlist.contains(sid)) { println("Already waitlisted"); return; }

        if (c.roster.size() >= c.capacity) {
            c.waitlist.add(sid);
            Main.audit("WAITLIST " + sid + "->" + cc);
            println("Course full. Added to WAITLIST.");
        } else {
            c.roster.add(sid);
            Main.audit("ENROLL " + sid + "->" + cc);
            println("Enrolled.");
        }
    }


    private static void dropUI(Scanner sc) {

        print("Student ID: ");
        int sid = Integer.parseInt(sc.nextLine());
        print("Course Code: ");
        String cc = sc.nextLine().trim();
        Course c = cRepo.getCourseById(Integer.parseInt(cc));
        if (c == null) { println("No such course"); return; }

        if (c.roster.remove(Optional.of(sid))) {
            Main.audit("DROP " + sid + " from " + cc);
            // Promote first waitlisted (FIFO)
            if (!c.waitlist.isEmpty()) {
                int promote = c.waitlist.remove(0);
                c.roster.add(promote);
                Main.audit("PROMOTE " + promote + "->" + cc);
                println("Promoted " + promote + " from waitlist.");
            } else {
                println("Dropped.");
            }
        } else if (c.waitlist.remove(Optional.of(sid))) {
            Main.audit("WAITLIST_REMOVE " + sid + " " + cc);
            println("Removed from waitlist.");
        } else {
            println("Not enrolled or waitlisted.");
        }
    }
}
