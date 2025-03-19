package org.quyetnt.finalprojecttdd.repository;

import org.quyetnt.finalprojecttdd.model.Course;
import org.quyetnt.finalprojecttdd.model.Registration;
import org.quyetnt.finalprojecttdd.model.RegistrationId;
import org.quyetnt.finalprojecttdd.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByStudent(Student student);

    Optional<Registration> findByStudentAndCourse(Student student, Course course);

    int countByStudent(Student student);
}
