package org.quyetnt.finalprojecttdd.repository;

import org.quyetnt.finalprojecttdd.model.ERole;
import org.quyetnt.finalprojecttdd.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole eRole);
}
