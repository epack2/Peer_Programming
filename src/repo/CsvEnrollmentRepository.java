package repo;

import model.Course;
import util.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class CsvEnrollmentRepository implements EnrollmentRepository {
    private final Path file;

    public CsvEnrollmentRepository(Path file) { this.file = file; }

    @Override
    public void saveAll(Collection<Course> courses) throws IOException {
        Files.createDirectories(file.getParent() == null ? Path.of(".") : file.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            for (Course c : courses) {
                for (String sid : c.getRoster()) {
                    bw.write(c.getCode() + "|" + sid + "|ENROLLED");
                    bw.newLine();
                }
                for (String sid : c.getWaitlist()) {
                    bw.write(c.getCode() + "|" + sid + "|WAITLIST");
                    bw.newLine();
                }
            }
        }
    }

    @Override
    public void loadEnrollmentsInto(Collection<Course> courses) throws IOException {
        if (!Files.exists(file)) return;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length >= 3) {
                    String code = p[0], sid = p[1], status = p[2];
                    Course c = courses.stream().filter(x -> x.getCode().equals(code)).findFirst().orElse(null);
                    if (c == null) {
                        Log.warn("Enrollment references unknown course: " + code);
                        continue;
                    }
                    if ("ENROLLED".equalsIgnoreCase(status)) {
                        if (!c.rosterContains(sid)) c.addToRoster(sid);
                    } else if ("WAITLIST".equalsIgnoreCase(status)) {
                        if (!c.waitlistContains(sid)) c.addToWaitlist(sid);
                    } else {
                        Log.warn("Skipping enrollment with unknown status: " + line);
                    }
                } else {
                    Log.warn("Skipping corrupted enrollment line: " + line);
                }
            }
        }
    }

    @Override
    public void importCsvOnce() { /* no-op */ }

    @Override
    public void close() { /* nothing */ }
}
