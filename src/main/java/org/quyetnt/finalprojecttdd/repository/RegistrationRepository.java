package org.quyetnt.finalprojecttdd.repository;

import org.quyetnt.finalprojecttdd.model.Registration;
import org.quyetnt.finalprojecttdd.model.RegistrationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, RegistrationId> {

    @Query("SELECT r FROM Registration r WHERE r.student.id = :studentId")
    List<Registration> findByStudentId(Long studentId);

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.student.id = :studentId")
    int countByStudentId(Long studentId);

    @Query("SELECT r FROM Registration r WHERE r.student.email = :email AND r.course.id = :courseId")
    Optional<Registration> findByStudentEmailAndCourseId(String email, Long courseId);
}
