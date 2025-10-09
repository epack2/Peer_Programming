import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.IO.println;

public class Course {
    private int courseNum;
    private String courseName;
    int capacity;
    List<Integer> roster;
    List<Integer> waitlist;

    /*
    String code, title; int capacity;
        List<String> roster = new ArrayList<>(), waitlist = new ArrayList<>();
        Course(String code, String title, int capacity) { this.code=code; this.title=title; this.capacity=capacity; }
     */

    public Course(int courseNum, String courseName, int capacity){
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.capacity = capacity;

        try (PrintWriter pw = new PrintWriter(new FileWriter("students.csv"))) {
            pw.println(this.courseNum + "," + this.courseName + "," + this.capacity);
        } catch (Exception e) {
            println("Failed save students: " + e.getMessage());
        }
    }

    public String getCourseName() {return courseName;}

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseNum() {return courseNum;}

    public void setCourseNum(int courseNum) {
        this.courseNum = courseNum;
    }




}
