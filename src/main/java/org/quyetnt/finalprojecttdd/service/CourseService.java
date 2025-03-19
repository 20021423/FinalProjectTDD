package org.quyetnt.finalprojecttdd.service;



import org.quyetnt.finalprojecttdd.dto.CourseDTO;
import org.quyetnt.finalprojecttdd.model.Course;
import org.quyetnt.finalprojecttdd.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + id));
    }

    public List<Course> findAllUncompletedCourses() {
        return courseRepository.findByEndTimeAfter(LocalDateTime.now());
    }

    public List<Course> findCoursesNotStartedYet() {
        return courseRepository.findByStartTimeAfter(LocalDateTime.now());
    }

    public CourseDTO convertToDTO(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getStartTime(),
                course.getEndTime(),
                course.getPrice()
        );
    }

    public List<CourseDTO> convertToDTOList(List<Course> courses) {
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}