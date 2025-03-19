package org.quyetnt.finalprojecttdd.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", columnDefinition = "NVARCHAR(255)")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "NVARCHAR(255)")
    private String lastName;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private Set<Registration> registrations = new HashSet<>();

    // Phương thức để lấy số lượng khóa học đã đăng ký
    public int getNumberOfRegisteredCourses() {
        return registrations.size();
    }
}
