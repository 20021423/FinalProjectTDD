package org.quyetnt.finalprojecttdd.controller;


import org.quyetnt.finalprojecttdd.dto.CourseDTO;
import org.quyetnt.finalprojecttdd.model.Course;
import org.quyetnt.finalprojecttdd.payload.response.ResponseObject;
import org.quyetnt.finalprojecttdd.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * API lấy tất cả các khóa học chưa kết thúc
     * @return ResponseEntity chứa danh sách khóa học chưa kết thúc
     */
    @GetMapping("/uncompleted")
    public ResponseEntity<ResponseObject<List<CourseDTO>>> getAllUncompletedCourses() {
        List<Course> courses = courseService.findAllUncompletedCourses();
        List<CourseDTO> courseDTOs = courseService.convertToDTOList(courses);

        ResponseObject<List<CourseDTO>> response = new ResponseObject<>(
                "success",
                "Lấy danh sách khóa học chưa kết thúc thành công",
                courseDTOs
        );

        return ResponseEntity.ok(response);
    }

    /**
     * API lấy tất cả các khóa học chưa bắt đầu
     * @return ResponseEntity chứa danh sách khóa học chưa bắt đầu
     */
    @GetMapping("/not-started")
    public ResponseEntity<ResponseObject<List<CourseDTO>>> getAllCoursesNotStarted() {
        List<Course> courses = courseService.findCoursesNotStartedYet();
        List<CourseDTO> courseDTOs = courseService.convertToDTOList(courses);

        ResponseObject<List<CourseDTO>> response = new ResponseObject<>(
                "success",
                "Lấy danh sách khóa học chưa bắt đầu thành công",
                courseDTOs
        );

        return ResponseEntity.ok(response);
    }

    /**
     * API lấy thông tin khóa học theo ID
     * @param id ID của khóa học
     * @return ResponseEntity chứa thông tin khóa học
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<CourseDTO>> getCourseById(@PathVariable Long id) {
        try {
            Course course = courseService.findCourseById(id);
            CourseDTO courseDTO = courseService.convertToDTO(course);

            ResponseObject<CourseDTO> response = new ResponseObject<>(
                    "success",
                    "Lấy thông tin khóa học thành công",
                    courseDTO
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ResponseObject<CourseDTO> response = new ResponseObject<>(
                    "error",
                    e.getMessage(),
                    null
            );

            return ResponseEntity.ok(response);
        }
    }
}
