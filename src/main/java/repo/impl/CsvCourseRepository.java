package repo.impl;

import model.Course;
import repo.CourseRepository;
import util.LoggerUtil;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * CSV format: code,title,capacity
 * Skips malformed lines (logs them). Loading does not touch enrollments.
 */
public class CsvCourseRepository implements CourseRepository {
    private final String filePath;
    private final Map<String, Course> cache = new LinkedHashMap<>();
    private boolean loaded = false;
    private static final Logger log = LoggerUtil.getLogger(CsvCourseRepository.class);

    public CsvCourseRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Course> findAll() {
        ensureLoaded();
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<Course> findByCode(String code) {
        ensureLoaded();
        return Optional.ofNullable(cache.get(code));
    }

    @Override
    public void save(Course course) throws Exception {
        ensureLoaded();
        cache.put(course.getCode(), course);
        saveAll(new ArrayList<>(cache.values()));
    }

    @Override
    public void saveAll(List<Course> courses) throws Exception {
        File f = new File(filePath);
        if (f.getParentFile() != null) f.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Course c : courses) {
                pw.println(c.getCode() + "," + c.getTitle() + "," + c.getCapacity());
            }
        }
    }

    @Override
    public List<Course> search(String query) {
        ensureLoaded();
        String q = query.toLowerCase();
        List<Course> out = new ArrayList<>();
        for (Course c : cache.values()) {
            if (c.getCode().toLowerCase().contains(q) || c.getTitle().toLowerCase().contains(q)) out.add(c);
        }
        return out;
    }

    @Override
    public void ensureLoaded() {
        if (loaded) return;
        cache.clear();
        File f = new File(filePath);
        if (!f.exists()) {
            log.info("Courses file not found, will create on save: " + filePath);
            loaded = true;
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 3) {
                    String code = p[0].trim(), title = p[1].trim();
                    try {
                        int cap = Integer.parseInt(p[2].trim());
                        cache.put(code, new Course(code, title, cap));
                    } catch (NumberFormatException nfe) {
                        log.warning("Skipping course with bad capacity: " + line);
                    }
                } else {
                    log.warning("Skipping malformed courses line: " + line);
                }
            }
            loaded = true;
            log.info("Loaded courses=" + cache.size());
        } catch (Exception e) {
            log.severe("Failed to load courses: " + e.getMessage());
            loaded = true;
        }
    }
}
