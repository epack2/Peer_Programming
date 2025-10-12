package repo;

import model.Course;

import java.util.List;

/**
 * Enrollment repository deals with loading / saving the roster & waitlist relationships.
 * Implementations should operate with Course objects (which contain roster & waitlist lists).
 */
public interface EnrollmentRepository {
    void loadEnrollments(List<Course> courses) throws Exception;
    void saveEnrollments(List<Course> courses) throws Exception;
    void ensureLoaded();
}
