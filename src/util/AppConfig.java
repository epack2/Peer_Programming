package util;

import java.nio.file.Path;

public final class AppConfig {
    // Read from system property or env var; fallback to defaults
    public static Path studentsFile() {
        String v = System.getProperty("reg.studentsFile", System.getenv("REG_STUDENTS_FILE"));
        return Path.of(v == null ? "students.csv" : v);
    }
    public static Path coursesFile() {
        String v = System.getProperty("reg.coursesFile", System.getenv("REG_COURSES_FILE"));
        return Path.of(v == null ? "courses.csv" : v);
    }
    public static Path enrollmentsFile() {
        String v = System.getProperty("reg.enrollmentsFile", System.getenv("REG_ENROLLMENTS_FILE"));
        return Path.of(v == null ? "enrollments.csv" : v);
    }
}
