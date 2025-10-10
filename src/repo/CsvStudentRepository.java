package repo;

import model.Student;
import util.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CsvStudentRepository implements StudentRepository {
    private final Path file;

    public CsvStudentRepository(Path file) {
        this.file = file;
    }

    @Override
    public void save(Student s) throws IOException {
        Map<String, Student> all = new LinkedHashMap<>();
        // load existing
        for (Student existing : findAll()) all.put(existing.getId(), existing);
        all.put(s.getId(), s);
        // rewrite file
        Files.createDirectories(file.getParent() == null ? Path.of(".") : file.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            for (Student st : all.values()) {
                bw.write(escape(st.getId()) + "," + escape(st.getName()) + "," + escape(st.getEmail()));
                bw.newLine();
            }
        }
    }

    @Override
    public Optional<Student> findById(String id) throws IOException {
        for (Student s : findAll()) if (s.getId().equals(id)) return Optional.of(s);
        return Optional.empty();
    }

    @Override
    public Collection<Student> findAll() throws IOException {
        List<Student> result = new ArrayList<>();
        if (!Files.exists(file)) return result;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                // CSV, simple - split on commas, allow empty fields
                String[] p = line.split(",", -1);
                if (p.length >= 3) {
                    result.add(new Student(unescape(p[0]), unescape(p[1]), unescape(p[2])));
                } else {
                    Log.warn("Skipping corrupted student line: " + line);
                }
            }
        } catch (IOException e) {
            Log.error("Failed reading students file: " + e.getMessage());
            throw e;
        }
        return result;
    }

    @Override
    public boolean existsById(String id) throws IOException {
        return findById(id).isPresent();
    }

    @Override
    public void importCsvOnce() {
        // no-op for now; if switching storage formats implement migration here.
    }

    @Override
    public void close() { /* nothing to close */ }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\n", " ").replace(",", "\\,");
    }
    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\,", ",");
    }
}
