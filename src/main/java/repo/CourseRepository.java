package repo;

import model.Course;

import java.util.*;

public interface CourseRepository {
    List<Course> findAll();
    Optional<Course> findByCode(String code);
    void save(Course course) throws Exception;
    void saveAll(List<Course> courses) throws Exception;
    List<Course> search(String query);
    void ensureLoaded();
}
