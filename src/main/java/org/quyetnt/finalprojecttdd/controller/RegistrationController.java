package org.quyetnt.finalprojecttdd.controller;


import org.quyetnt.finalprojecttdd.dto.CourseDTO;

import org.quyetnt.finalprojecttdd.payload.request.CourseRegistrationRequest;
import org.quyetnt.finalprojecttdd.payload.response.ResponseObject;

import org.quyetnt.finalprojecttdd.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject<List<CourseDTO>>> registerCourse(@RequestBody CourseRegistrationRequest request) {
        ResponseObject<List<CourseDTO>> response = registrationService.registerCourse(request.getEmail(), request.getCourseId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/unregister/{courseId}/{email}")
    public ResponseEntity<ResponseObject<?>> unregisterCourse(@PathVariable Long courseId, @PathVariable String email) {
        ResponseObject<?> response = registrationService.unregisterCourse(courseId, email);
        return ResponseEntity.ok(response);
    }
}
