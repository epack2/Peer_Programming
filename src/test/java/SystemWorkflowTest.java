import exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repo.CourseRepository;
import repo.EnrollmentRepository;
import repo.StudentRepository;
import service.RegistrationService;
import model.Student;
import model.Course;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SystemWorkflowTest {

    @Mock
    StudentRepository studentRepository;

    @Mock
    CourseRepository courseRepository;

    @Mock
    EnrollmentRepository enrollmentRepository;

    @InjectMocks
    RegistrationService Registration;



    @Test
    public void fullSystemRun() throws Exception
    {
        //Create the student and Courses
        Student testStudent1 = new Student("B01", "Student Test", "test@uca.edu");
        Student testStudent2 = new Student("B02", "Test Student", "testemail@test.email");
        Course testCourse1 = new Course("CSCI4490", "Software Engineering", 1);
        Course testCourse2 = new Course("CSCI4371", "Machine Learning", 1);


        //Describe behavior
        when(studentRepository.findById("B01")).thenReturn(Optional.of(testStudent1));
        when(studentRepository.findById("B02")).thenReturn(Optional.of(testStudent2));
        when(courseRepository.findByCode("CSCI4490")).thenReturn(Optional.of(testCourse1));
        when(courseRepository.findByCode("CSCI4371")).thenReturn(Optional.of(testCourse2));
        when(courseRepository.findAll()).thenReturn(List.of(testCourse1, testCourse2));
        when(studentRepository.findAll()).thenReturn(List.of(testStudent1, testStudent2));


        assertTrue(Registration.enroll("B01", "CSCI4490"));
        assertTrue(Registration.enroll("B01", "CSCI4371"));
        assertFalse(Registration.enroll("B02", "CSCI4490"));


        List<Course> courses = Registration.listCourses();
        assertTrue(courses.stream().anyMatch(c -> c.getCode().equals("CSCI4490")));
        assertTrue(courses.stream().anyMatch(c -> c.getCode().equals("CSCI4371")));

        List<Student> students = Registration.listStudents();
        assertTrue(students.stream().anyMatch(c -> c.getId().equals("B01")));
        assertTrue(students.stream().anyMatch(c -> c.getId().equals("B02")));

        boolean promoted = Registration.drop("B01", "CSCI4490");

        // Assert
        assertTrue(promoted);
        verify(courseRepository, times(4)).save(any());




    }
}
