import java.io.FileWriter;
import java.io.PrintWriter;

import static java.lang.IO.println;

public class Student {
    private int id;
    private String name;
    private String email;

    public Student(int id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;
        try (PrintWriter pw = new PrintWriter(new FileWriter("students.csv"))) {
            pw.println(this.id + "," + this.name + "," + this.email);
        } catch (Exception e) {
            println("Failed save students: " + e.getMessage());
        }
    }

    public int getId() {return id;}

    public String getName() {return name;}

    public String toString() { return id + " " + name + " <" + email + ">"; }

}
