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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PerformanceTest {
    @Mock
    StudentRepository studentRepository;

    @Mock
    CourseRepository courseRepository;

    @Mock
    EnrollmentRepository enrollmentRepository;

    @InjectMocks
    RegistrationService Registration;

    private List<Student> students;
    private Course course;

    @BeforeEach
    void setUp()
    {

        course = new Course("CSCI4490", "Software Engineering", 1);

        when(courseRepository.findByCode("CSCI4490")).thenReturn(Optional.of(course));
        when(courseRepository.findAll()).thenReturn(List.of(course));


        students = new ArrayList<>();
        int i = 0;
        while(i < 50) {
            Student s = new Student("B" + i, "Student number", "student" + i + "@test.edu");
            students.add(s);
            when(studentRepository.findById(s.getId())).thenReturn(Optional.of(s));
            i += 1;
        };

    }

    @Test
    public void enrollMultipleStudentsPerformance()
    {
        long start = System.nanoTime();

        for (Student s: students) {
            Registration.enroll(s.getId(), "CSCI4490");
        }

        long end = System.nanoTime();
        long durationMs = (end - start) / 1000000;

        System.out.println("Time to enroll 50 students: " + durationMs + " ms");

    }
}
