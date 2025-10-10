package repo;

import model.Student;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public interface StudentRepository {
    void save(Student s) throws IOException;
    Optional<Student> findById(String id) throws IOException;
    Collection<Student> findAll() throws IOException;
    boolean existsById(String id) throws IOException;
    void importCsvOnce() throws IOException; // migration hook (may be no-op)
    void close() throws IOException;
}
