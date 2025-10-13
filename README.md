# UCA Course Registration System (Refactored)

This is a modular Java CLI application for managing students, courses, and enrollments. 
The refactor separates responsibilities into layers and various classes for maintainability, testability, and scalability.

## Features

- Add/List Students (Banner ID, Name, Email)
- Add/List Courses (Code, Title, Capacity)
- Enroll/Drop Students (with waitlist support)
- Search for students/courses
- File persistence (CSV)
- Demo mode with seeded data

## Project Structure
```
main
└── java
    ├── app
    │   └── MainCLI.java
    ├── exception
    │   ├── EnrollmentException.java
    │   └── ValidationException.java
    ├── model
    │   ├── Course.java
    │   └── Student.java
    ├── repo
    │   ├── CourseRepository.java
    │   ├── EnrollmentRepository.java
    │   ├── impl
    │   │   ├── CsvCourseRepository.java
    │   │   ├── CsvEnrollmentRepository.java
    │   │   └── CsvStudentRepository.java
    │   └── StudentRepository.java
    ├── service
    │   └── RegistrationService.java
    └── util
        ├── Config.java
        ├── LoggerUtil.java
        └── ValidationUtil.java
```

## How to Run

### Build
mvn -q -DskipTests package 

### Run 

java -jar target/course-registration-0.1.0.jar # uses configured storage

### Seeded demo

java -jar target/course-registration-0.1.0.jar --demo

# Alternative Run

You can use your IDE (eg. IntelliJ, VS Code,etc) and run the MainCLI.java file.


# Project Owner
- Pranaya Pudasaini
- Ethan Pack
- Yapi Joas Samuel N'guessan

