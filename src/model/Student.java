package model;

import util.ValidationUtil;

/**
 * Domain model: Student (no UI/persistence logic here).
 */
public class Student {
    private final String id;
    private final String name;
    private final String email;

    public Student(String id, String name, String email) {
        // Basic defensiveness; service layer will also validate more fully
        ValidationUtil.requireNonEmpty(id, "Banner ID");
        ValidationUtil.requireNonEmpty(name, "Name");
        ValidationUtil.requireNonEmpty(email, "Email");

        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return id + " " + name + " <" + email + ">";
    }
}
