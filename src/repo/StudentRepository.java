package repo;


import model.Student;
import java.util.*;


public interface StudentRepository {
    Optional<Student> findByBannerId(String bannerId);
    Optional<Student> findByName(String name);
    List<Student> findAll();
    void save(Student student); // upsert
    void delete(String bannerId);
}