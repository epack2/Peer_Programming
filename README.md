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

src/
├─ app/
│ └─ MainCLI.java # CLI entry point (**MAIN ENTRY PONT**)
├─ model/
│ ├─ Student.java
│ └─ Course.java
├─ repo/
│ ├─ StudentRepository.java
│ ├─ CourseRepository.java
│ ├─ EnrollmentRepository.java
│ └─ impl/ # CSV/JSON implementations
├─ service/
│ └─ RegistrationService.java
├─ util/
│ ├─ Config.java
│ └─ LoggerUtil.java
└─ exception/
├─ ValidationException.java
└─ EnrollmentException.java

