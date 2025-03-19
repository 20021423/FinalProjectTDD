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

    @Transactional
    public ResponseObject<List<CourseDTO>> registerCourse(String email, Long courseId) {
        Optional<Student> studentOptional = studentRepository.findByEmail(email);
        if (studentOptional.isEmpty()) {
            return new ResponseObject<>("error", "Không tìm thấy học viên với email đã cung cấp", null);
        }

        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return new ResponseObject<>("error", "Không tìm thấy khóa học với ID đã cung cấp", null);
        }

        Student student = studentOptional.get();
        Course course = courseOptional.get();

        Optional<Registration> existingRegistration = registrationRepository.findByStudentAndCourse(student, course);
        if (existingRegistration.isPresent()) {
            return new ResponseObject<>("error", "Học viên đã đăng ký khóa học này", null);
        }

        if (course.getStartTime().isBefore(LocalDateTime.now())) {
            return new ResponseObject<>("error", "Không thể đăng ký khóa học đã bắt đầu", null);
        }

        BigInteger price = course.getPrice();
        String message = "Đăng ký khóa học thành công";

        int registeredCoursesCount = registrationRepository.countByStudent(student);
        if (registeredCoursesCount >= 2) {
            price = price.multiply(BigInteger.valueOf(100 - DISCOUNT_PERCENTAGE)).divide(HUNDRED);
            message = "Đăng ký khóa học thành công với giảm giá 25%";
        }

        Registration registration = new Registration();
        registration.setStudent(student);
        registration.setCourse(course);
        registration.setPrice(price);
        registration.setRegisteredDate(LocalDateTime.now());

        registrationRepository.save(registration);

        List<Course> uncompletedCourses = courseService.findAllUncompletedCourses();
        List<CourseDTO> courseDTOs = courseService.convertToDTOList(uncompletedCourses);

        return new ResponseObject<>("success", message, courseDTOs);
    }

    @Transactional
    public ResponseObject<String> unregisterCourse(Long courseId, String email) {
        Optional<Student> studentOptional = studentRepository.findByEmail(email);
        if (studentOptional.isEmpty()) {
            return new ResponseObject<>("error", "Không tìm thấy học viên với email đã cung cấp", null);
        }

        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return new ResponseObject<>("error", "Không tìm thấy khóa học với ID đã cung cấp", null);
        }

        Student student = studentOptional.get();
        Course course = courseOptional.get();

        Optional<Registration> registrationOptional = registrationRepository.findByStudentAndCourse(student, course);
        if (registrationOptional.isEmpty()) {
            return new ResponseObject<>("error", "Học viên chưa đăng ký khóa học này", null);
        }

        registrationRepository.delete(registrationOptional.get());

        return new ResponseObject<>("success", "Hủy đăng ký khóa học thành công", null);
    }
}