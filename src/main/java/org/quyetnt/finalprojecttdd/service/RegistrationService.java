package org.quyetnt.finalprojecttdd.service;


import org.quyetnt.finalprojecttdd.dto.CourseDTO;

import org.quyetnt.finalprojecttdd.model.Course;
import org.quyetnt.finalprojecttdd.model.Registration;
import org.quyetnt.finalprojecttdd.model.Student;
import org.quyetnt.finalprojecttdd.payload.response.ResponseObject;

import org.quyetnt.finalprojecttdd.repository.CourseRepository;
import org.quyetnt.finalprojecttdd.repository.RegistrationRepository;
import org.quyetnt.finalprojecttdd.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    private static final BigInteger HUNDRED = BigInteger.valueOf(100);
    private static final int DISCOUNT_PERCENTAGE = 25;

    /**
     * Đăng ký khóa học cho học viên
     *
     * @param email    Email của học viên
     * @param courseId ID của khóa học
     * @return ResponseObject chứa danh sách khóa học chưa hoàn thành
     */
    @Transactional
    public ResponseObject<List<CourseDTO>> registerCourse(String email, Long courseId) {
        // Tìm học viên theo email
        Optional<Student> studentOptional = studentRepository.findByEmail(email);
        if (studentOptional.isEmpty()) {
            return new ResponseObject<>("error", "Không tìm thấy học viên với email đã cung cấp", null);
        }

        // Tìm khóa học theo ID
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return new ResponseObject<>("error", "Không tìm thấy khóa học với ID đã cung cấp", null);
        }

        Student student = studentOptional.get();
        Course course = courseOptional.get();

        // Kiểm tra xem học viên đã đăng ký khóa học này chưa
        Optional<Registration> existingRegistration = registrationRepository.findByStudentAndCourse(student, course);
        if (existingRegistration.isPresent()) {
            return new ResponseObject<>("error", "Học viên đã đăng ký khóa học này", null);
        }

        // Kiểm tra xem khóa học đã bắt đầu chưa
        if (course.getStartTime().isBefore(LocalDateTime.now())) {
            return new ResponseObject<>("error", "Không thể đăng ký khóa học đã bắt đầu", null);
        }

        // Tính giá tiền và kiểm tra xem có được giảm giá hay không
        BigInteger price = course.getPrice();
        String message = "Đăng ký khóa học thành công";

        // Kiểm tra số lượng khóa học đã đăng ký
        int registeredCoursesCount = registrationRepository.countByStudent(student);
        if (registeredCoursesCount >= 2) {
            // Áp dụng giảm giá 25% cho khóa học thứ 3 trở đi
            price = price.multiply(BigInteger.valueOf(100 - DISCOUNT_PERCENTAGE)).divide(HUNDRED);
            message = "Đăng ký khóa học thành công với giảm giá 25%";
        }

        // Tạo đăng ký mới
        Registration registration = new Registration();
        registration.setStudent(student);
        registration.setCourse(course);
        registration.setPrice(price);
        registration.setRegisteredDate(LocalDateTime.now());

        // Lưu đăng ký
        registrationRepository.save(registration);

        // Trả về danh sách khóa học chưa hoàn thành
        List<Course> uncompletedCourses = courseService.findAllUncompletedCourses();
        List<CourseDTO> courseDTOs = courseService.convertToDTOList(uncompletedCourses);

        return new ResponseObject<>("success", message, courseDTOs);
    }

    /**
     * Hủy đăng ký khóa học
     *
     * @param courseId ID của khóa học
     * @param email    Email của học viên
     * @return ResponseObject
     */
    @Transactional
    public ResponseObject<String> unregisterCourse(Long courseId, String email) {
        // Tìm học viên theo email
        Optional<Student> studentOptional = studentRepository.findByEmail(email);
        if (studentOptional.isEmpty()) {
            return new ResponseObject<>("error", "Không tìm thấy học viên với email đã cung cấp", null);
        }

        // Tìm khóa học theo ID
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return new ResponseObject<>("error", "Không tìm thấy khóa học với ID đã cung cấp", null);
        }

        Student student = studentOptional.get();
        Course course = courseOptional.get();

        // Kiểm tra xem học viên đã đăng ký khóa học này chưa
        Optional<Registration> registrationOptional = registrationRepository.findByStudentAndCourse(student, course);
        if (registrationOptional.isEmpty()) {
            return new ResponseObject<>("error", "Học viên chưa đăng ký khóa học này", null);
        }

        // Xóa đăng ký
        registrationRepository.delete(registrationOptional.get());

        return new ResponseObject<>("success", "Hủy đăng ký khóa học thành công", null);
    }
}