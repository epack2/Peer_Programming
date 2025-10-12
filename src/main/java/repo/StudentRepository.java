package repo;

import model.Student;

import java.util.*;

/**
 * Repository interface for students.
 */
public interface StudentRepository {
    List<Student> findAll();
    Optional<Student> findById(String id);
    void save(Student student) throws Exception;
    void saveAll(List<Student> students) throws Exception;
    List<Student> search(String query);
    void ensureLoaded(); // load from underlying storage if needed
}
