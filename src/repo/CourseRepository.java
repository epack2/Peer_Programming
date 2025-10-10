package repo;

import model.Course;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public interface CourseRepository {
    void save(Course c) throws IOException;
    Optional<Course> findByCode(String code) throws IOException;
    Collection<Course> findAll() throws IOException;
    boolean existsByCode(String code) throws IOException;
    void importCsvOnce() throws IOException;
    void close() throws IOException;
}
