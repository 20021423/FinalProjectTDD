package org.quyetnt.finalprojecttdd.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(255)")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Registration> registrations = new HashSet<>();

    // Phương thức kiểm tra khóa học đã bắt đầu chưa
    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(startTime);
    }

    // Phương thức kiểm tra khóa học đã kết thúc chưa
    public boolean isCompleted() {
        return LocalDateTime.now().isAfter(endTime);
    }
}