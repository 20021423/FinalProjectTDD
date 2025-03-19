package org.quyetnt.finalprojecttdd.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
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

    @Column(name = "description", columnDefinition = "VARCHAR(255)")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "price", nullable = false)
    private BigInteger price;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Registration> registrations = new HashSet<>();

    public Course(long l, String javaProgramming, String learnJavaBasics, LocalDateTime localDateTime, LocalDateTime localDateTime1, BigInteger bigInteger) {
        this.id = l;
        this.name = javaProgramming;
        this.description = learnJavaBasics;
        this.startTime = localDateTime;
        this.endTime = localDateTime1;
        this.price = bigInteger;
    }
}
