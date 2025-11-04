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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RegistrationTest {

    @Mock
    StudentRepository studentRepository;

    @Mock
    CourseRepository courseRepository;

    @Mock
    EnrollmentRepository enrollmentRepository;

    @InjectMocks
    RegistrationService Registration;

    private Student testStudent;
    private Course testCourse;

    @BeforeEach
    void setUp()
    {
        testStudent = new Student("B01", "Ben Ten", "test@uca.edu");
        testCourse = new Course("CSCI4490", "Software Engineering", 1);
    }

    @Test
    public void addAStudent() throws Exception
    {
        when(studentRepository.findById("B01")).thenReturn(Optional.empty());

        Registration.addStudent("B01", "Test Student", "test@test.com");

        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void addAStudentDuplicate() throws Exception
    {
        when(studentRepository.findById("B01")).thenReturn(Optional.of(new Student("B01", "Test Student", "test@test.email")));


        assertThrows(ValidationException.class,
                () -> {
                    Registration.addStudent("B01", "Test Student", "test@test.com");
                });

        verify(studentRepository, never()).save(any(Student.class));

    }

    @Test
    public void addAStudentRuntimeException() throws Exception
    {
        when(studentRepository.findById("B02")).thenReturn(Optional.empty());
        doThrow(new Exception("Runtime Exception")).when(studentRepository).save(any(Student.class));


        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            Registration.addStudent("B02", "Test Bob", "bob@test.email");
        });

        assertTrue(ex.getMessage().contains("Failed to save student"));
    }

    @Test
    public void addAClass() throws Exception
    {
        when(courseRepository.findByCode("CSCI4490")).thenReturn(Optional.empty());

        Registration.addCourse("CSCI4490", "Software Engineering", 1);

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    public void addACourseDuplicate() throws Exception
    {
        when(courseRepository.findByCode("CSCI4490")).thenReturn(Optional.of(new Course("CSCI4490", "Software Engineering", 1)));

        assertThrows(ValidationException.class,
                () -> {
                    Registration.addCourse("CSCI4490", "Software Engineering", 1);
                });

        verify(courseRepository, never()).save(any(Course.class));

    }

    @Test
    public void addACourseRuntimeException() throws Exception
    {
        when(courseRepository.findByCode("CSCI4490")).thenReturn(Optional.empty());
        doThrow(new Exception("Runtime Exception")).when(courseRepository).save(any(Course.class));


        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            Registration.addCourse("CSCI4490", "Software Engineering", 1);
        });

        assertTrue(ex.getMessage().contains("Failed to save course"));
    }



    @Test
    public void enrollAStudent() throws Exception
    {

        when(studentRepository.findById("B01")).thenReturn(Optional.of(testStudent));
        when(courseRepository.findByCode("CSCI4490")).thenReturn(Optional.of(testCourse));


        boolean result = Registration.enroll("B01", "CSCI4490");

        // Assert
        assertTrue(result);
        verify(courseRepository, times(1)).save(testCourse);
        verify(studentRepository, times(1)).findById("B01");
    }

    @Test
    public void enrollStudentWhenFull() throws Exception
    {
        testCourse.addToRoster("B02");
        when(studentRepository.findById("B01")).thenReturn(Optional.of(testStudent));
        when(courseRepository.findByCode("CSCI4490")).thenReturn(Optional.of(testCourse));


        boolean result = Registration.enroll("B01", "CSCI4490");

        // Assert
        assertFalse(result);
        verify(courseRepository, times(1)).save(testCourse);

    }



    @Test
    public void dropStudentAndPromoteWaitlist() throws Exception
    {
        testCourse.addToRoster("B01");
        testCourse.addToWaitlist("B02");
        when(courseRepository.findByCode("CSCI4490")).thenReturn(Optional.of(testCourse));


        boolean result = Registration.drop("B01", "CSCI4490");

        // Assert
        assertTrue(result);
        verify(courseRepository).save(testCourse);

    }











}
