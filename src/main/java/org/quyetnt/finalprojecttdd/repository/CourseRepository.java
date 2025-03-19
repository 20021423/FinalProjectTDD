package org.quyetnt.finalprojecttdd.repository;

import org.quyetnt.finalprojecttdd.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStartTimeAfter(LocalDateTime now);

    List<Course> findByEndTimeAfter(LocalDateTime now);
}
