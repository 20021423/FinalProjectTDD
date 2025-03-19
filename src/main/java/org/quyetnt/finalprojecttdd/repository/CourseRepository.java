package org.quyetnt.finalprojecttdd.repository;

import org.quyetnt.finalprojecttdd.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // Tìm tất cả các khóa học chưa bắt đầu
    List<Course> findByStartTimeAfter(LocalDateTime now);

    // Tìm tất cả các khóa học chưa kết thúc
    List<Course> findByEndTimeAfter(LocalDateTime now);
}
