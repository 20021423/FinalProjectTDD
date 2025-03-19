package org.quyetnt.finalprojecttdd.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    @EmbeddedId
    private RegistrationId id;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "registered_date", nullable = false)
    private LocalDateTime registeredDate;

    // Constructor để tạo Registration từ Student và Course
    public Registration(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.id = new RegistrationId(student.getId(), course.getId());
        this.registeredDate = LocalDateTime.now();
        this.price = course.getPrice(); // Giá mặc định, có thể áp dụng giảm giá sau
    }

    // Áp dụng giảm giá
    public void applyDiscount(double discountPercentage) {
        BigDecimal discountFactor = BigDecimal.valueOf(1 - discountPercentage);
        this.price = this.course.getPrice().multiply(discountFactor);
    }
}
