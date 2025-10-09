import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.IO.print;
import static java.lang.IO.println;

public class StudentRepository {
    public List<Student> getAllStudents()
    {
        String csvFile = "students.csv";
        List<Student> students = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 3) {
                    int id = Integer.parseInt(p[0]);
                    String name = p[1];
                    String email = p[2];
                    students.add(new Student(id, name, email));
                }
            }
            Main.audit("LOAD students=" + students.size());
        } catch (Exception e) {
            println("Failed load students: " + e.getMessage());
        }
        int i = 0;
        while (i < students.size())
        {
            students.get(i).toString();
        }
        return students;
    }



    private void listStudents() {
        List<Student> students;
        students = getAllStudents();
        println("Students:");
        for (Student s : students) println(" - " + s);
    }


    Student getStudentById(int id)
    {
        return (Student) getAllStudents().stream().filter(student -> student.getId() == id);
    }

    private static void addStudentUI(Scanner sc) {
        print("Banner ID: ");
        int id = Integer.parseInt(sc.nextLine());
        print("Name: ");
        String name = sc.nextLine().trim();
        print("Email: ");
        String email = sc.nextLine().trim();
        Student s = new Student(id, name, email);
    }


}
