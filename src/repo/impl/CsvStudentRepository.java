package repo.impl;

import model.Student;
import repo.StudentRepository;
import util.LoggerUtil;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * CSV format: id,name,email
 * Skips malformed lines (logs them).
 */
public class CsvStudentRepository implements StudentRepository {
    private final String filePath;
    private final Map<String, Student> cache = new LinkedHashMap<>();
    private boolean loaded = false;
    private static final Logger log = LoggerUtil.getLogger(CsvStudentRepository.class);

    public CsvStudentRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Student> findAll() {
        ensureLoaded();
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<Student> findById(String id) {
        ensureLoaded();
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public void save(Student student) throws Exception {
        ensureLoaded();
        cache.put(student.getId(), student);
        saveAll(new ArrayList<>(cache.values()));
    }

    @Override
    public void saveAll(List<Student> students) throws Exception {
        File f = new File(filePath);
        // ensure parent exists
        if (f.getParentFile() != null) f.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Student s : students) {
                pw.println(s.getId() + "," + s.getName() + "," + s.getEmail());
            }
        }
    }

    @Override
    public List<Student> search(String query) {
        ensureLoaded();
        String q = query.toLowerCase();
        List<Student> out = new ArrayList<>();
        for (Student s : cache.values()) {
            if (s.getId().toLowerCase().contains(q) || s.getName().toLowerCase().contains(q)) out.add(s);
        }
        return out;
    }

    @Override
    public void ensureLoaded() {
        if (loaded) return;
        cache.clear();
        File f = new File(filePath);
        if (!f.exists()) {
            log.info("Students file not found, will create on save: " + filePath);
            loaded = true;
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                // tolerant split
                String[] p = line.split(",", -1);
                if (p.length >= 3) {
                    String id = p[0].trim(), name = p[1].trim(), email = p[2].trim();
                    if (!id.isEmpty()) cache.put(id, new Student(id, name, email));
                } else {
                    log.warning("Skipping malformed students line: " + line);
                }
            }
            loaded = true;
            log.info("Loaded students=" + cache.size());
        } catch (Exception e) {
            log.severe("Failed to load students: " + e.getMessage());
            loaded = true; // avoid retry loop; treat as empty
        }
    }
}
