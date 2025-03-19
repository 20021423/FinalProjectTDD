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

    /**
     * Tìm khóa học theo ID
     * @param id ID của khóa học
     * @return Khóa học tìm thấy
     * @throws RuntimeException nếu không tìm thấy khóa học
     */
    public Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + id));
    }

    /**
     * Lấy danh sách tất cả các khóa học chưa kết thúc
     * @return Danh sách khóa học chưa kết thúc
     */
    public List<Course> findAllUncompletedCourses() {
        return courseRepository.findByEndTimeAfter(LocalDateTime.now());
    }

    /**
     * Lấy danh sách tất cả các khóa học chưa bắt đầu
     * @return Danh sách khóa học chưa bắt đầu
     */
    public List<Course> findCoursesNotStartedYet() {
        return courseRepository.findByStartTimeAfter(LocalDateTime.now());
    }

    /**
     * Chuyển đổi Course thành CourseDTO
     * @param course Đối tượng Course
     * @return Đối tượng CourseDTO
     */
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

    /**
     * Chuyển đổi danh sách Course thành danh sách CourseDTO
     * @param courses Danh sách Course
     * @return Danh sách CourseDTO
     */
    public List<CourseDTO> convertToDTOList(List<Course> courses) {
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}