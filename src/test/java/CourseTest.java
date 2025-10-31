import model.Course;
import org.junit.jupiter.api.Test;
import repo.CourseRepository;
import repo.EnrollmentRepository;
import repo.StudentRepository;
import service.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;


public class CourseTest {

    @Test
    public void enrollToClassAndCapacity()
    {
        Course c = new Course("CS101", "Intro", 2);
        assertTrue(c.addToRoster("B01"));
        assertTrue(c.addToRoster("B02"));
        assertFalse(c.addToRoster("B02"));
        assertTrue(c.isFull());
    }

    @Test
    public void addToWaitList()
    {
        Course c = new Course("CS102", "Data", 2);
        assertTrue(c.addToRoster("B01"));
        assertTrue(c.addToRoster("B02"));
        assertTrue(c.addToWaitlist("B03"));
        assertTrue(c.addToWaitlist("B04"));
        assertFalse(c.addToWaitlist("B04"));
        assertTrue(c.rosterContains("B01"));
        assertTrue(c.waitlistContains("B03"));

    }



}
