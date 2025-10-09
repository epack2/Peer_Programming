import java.util.Scanner;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.IO.print;
import static java.lang.IO.println;

public class CourseRepository {

    public List<Course> getAllCourses()
    {
        String csvFile = "courses.csv";
        List<Course> courses = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 3) {
                    int courseNum = Integer.parseInt(p[0]);
                    String courseName = p[1];
                    int capacity = Integer.parseInt(p[2]);
                    courses.add(new Course(courseNum, courseName, capacity));
                }
            }
            Main.audit("LOAD students=" + courses.size());
        } catch (Exception e) {
            println("Failed load students: " + e.getMessage());
        }
        int i = 0;
        while (i < courses.size())
        {
            courses.get(i).toString();
        }
        return courses;
    }

    Course getCourseById(int courseNum)
    {
        return (Course) getAllCourses().stream().filter(course -> course.getCourseNum() == courseNum);
    }

    private static void addCourseUI(Scanner sc) {
        print("Course Code: ");
        int courseNum = Integer.parseInt(sc.nextLine());
        print("Title: ");
        String courseName = sc.nextLine().trim();
        print("Capacity: ");
        int capacity = Integer.parseInt(sc.nextLine().trim());
        Course c = new Course(courseNum, courseName, capacity);

    }



}
