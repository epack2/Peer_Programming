package repo;

import model.Course;
import util.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CsvCourseRepository implements CourseRepository {
    private final Path file;

    public CsvCourseRepository(Path file) { this.file = file; }

    @Override
    public void save(Course c) throws IOException {
        Map<String, Course> all = new LinkedHashMap<>();
        for (Course ex : findAll()) all.put(ex.getCode(), ex);
        all.put(c.getCode(), c);
        Files.createDirectories(file.getParent() == null ? Path.of(".") : file.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            for (Course course : all.values()) {
                bw.write(escape(course.getCode()) + "," + escape(course.getTitle()) + "," + course.getCapacity());
                bw.newLine();
            }
        }
    }

    @Override
    public Optional<Course> findByCode(String code) throws IOException {
        for (Course c : findAll()) if (c.getCode().equals(code)) return Optional.of(c);
        return Optional.empty();
    }

    @Override
    public Collection<Course> findAll() throws IOException {
        List<Course> result = new ArrayList<>();
        if (!Files.exists(file)) return result;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 3) {
                    try {
                        int cap = Integer.parseInt(p[2].trim());
                        result.add(new Course(unescape(p[0]), unescape(p[1]), cap));
                    } catch (NumberFormatException ex) {
                        Log.warn("Skipping course with invalid capacity: " + line);
                    }
                } else {
                    Log.warn("Skipping corrupted course line: " + line);
                }
            }
        }
        return result;
    }

    @Override
    public boolean existsByCode(String code) throws IOException {
        return findByCode(code).isPresent();
    }

    @Override
    public void importCsvOnce() { /* no-op */ }

    @Override
    public void close() { /* nothing */ }

    private static String escape(String s) { return s == null ? "" : s.replace("\\", "\\\\").replace(",", "\\,"); }
    private static String unescape(String s) { return s == null ? "" : s.replace("\\,", ",").replace("\\\\", "\\"); }
}
