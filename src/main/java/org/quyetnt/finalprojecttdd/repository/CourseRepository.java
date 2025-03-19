package org.quyetnt.finalprojecttdd.repository;

import org.quyetnt.finalprojecttdd.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE c.endTime > :now")
    List<Course> findAllUncompletedCourses(LocalDateTime now);

    @Query("SELECT c FROM Course c WHERE c.startTime > :now")
    List<Course> findAllUpcomingCourses(LocalDateTime now);
}
