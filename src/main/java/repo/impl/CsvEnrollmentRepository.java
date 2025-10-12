package repo.impl;

import model.Course;
import repo.EnrollmentRepository;
import util.LoggerUtil;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Enrollment CSV lines: courseCode|studentId|ENROLLED or WAITLIST
 * Loading applies to Course objects passed in (populates their roster & waitlist).
 * Saving writes out the roster and waitlist entries.
 */
public class CsvEnrollmentRepository implements EnrollmentRepository {
    private final String filePath;
    private boolean loaded = false;
    private static final Logger log = LoggerUtil.getLogger(CsvEnrollmentRepository.class);

    public CsvEnrollmentRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void loadEnrollments(List<Course> courses) throws Exception {
        if (loaded) return;
        Map<String, Course> byCode = new HashMap<>();
        for (Course c : courses) byCode.put(c.getCode(), c);

        File f = new File(filePath);
        if (!f.exists()) {
            log.info("Enrollments file not found, will create on save: " + filePath);
            loaded = true;
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length >= 3) {
                    String code = p[0].trim(), sid = p[1].trim(), status = p[2].trim();
                    Course c = byCode.get(code);
                    if (c == null) {
                        log.warning("Enrollment refers to unknown course, skipping: " + line);
                        continue;
                    }
                    if ("ENROLLED".equalsIgnoreCase(status)) {
                        if (!c.rosterContains(sid)) c.addToRoster(sid);
                    } else if ("WAITLIST".equalsIgnoreCase(status)) {
                        if (!c.waitlistContains(sid)) c.addToWaitlist(sid);
                    } else {
                        log.warning("Unknown enrollment status, skipping: " + line);
                    }
                } else {
                    log.warning("Skipping malformed enrollment line: " + line);
                }
            }
            loaded = true;
            log.info("Loaded enrollments");
        } catch (Exception e) {
            log.severe("Failed to load enrollments: " + e.getMessage());
            loaded = true;
        }
    }

    @Override
    public void saveEnrollments(List<Course> courses) throws Exception {
        File f = new File(filePath);
        if (f.getParentFile() != null) f.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Course c : courses) {
                for (String sid : c.getRoster()) pw.println(c.getCode() + "|" + sid + "|ENROLLED");
                for (String sid : c.getWaitlist()) pw.println(c.getCode() + "|" + sid + "|WAITLIST");
            }
        }
    }

    @Override
    public void ensureLoaded() {
        // no-op — loadEnrollments is explicitly called by service with courses
    }
}
