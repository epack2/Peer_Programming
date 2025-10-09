package model;


import java.util.*;
import util.ValidationUtil;



public final class Student {
    private final String bannerId; // unique identifier (e.g. B12345678)
    private final String name;
    private final List<Integer> grades = new ArrayList<>();


    public Student(String bannerId, String name) {
        ValidationUtil.requireNonEmpty(name, "name");
        ValidationUtil.requireBannerId(bannerId);
        this.bannerId = bannerId.trim();
        this.name = name.trim();
    }


    public String getBannerId() { return bannerId; }


    public String getName() { return name; }


    public void addGrade(int grade) {
        ValidationUtil.requireGradeInRange(grade);
        grades.add(grade);
    }


    public List<Integer> getGrades() {
        return (grades);
    }


    public double average() {
        if (grades.isEmpty()) return 0.0;
        return grades.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }


    @Override
    public String toString() {
        return String.format("%s (%s) | grades=%s | avg=%.2f", name, bannerId, grades, average());
    }
}