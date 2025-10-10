package repo;

import model.Course;

import java.io.IOException;
import java.util.Collection;

public interface EnrollmentRepository {
    void saveAll(Collection<Course> courses) throws IOException;
    void loadEnrollmentsInto(Collection<Course> courses) throws IOException;
    void importCsvOnce() throws IOException;
    void close() throws IOException;
}
