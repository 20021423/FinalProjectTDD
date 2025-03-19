package org.quyetnt.finalprojecttdd.config;

import org.quyetnt.finalprojecttdd.model.ERole;
import org.quyetnt.finalprojecttdd.model.Role;
import org.quyetnt.finalprojecttdd.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInitializer {

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role(ERole.ROLE_USER));
                roleRepository.save(new Role(ERole.ROLE_ADMIN));
                System.out.println("Roles initialized.");
            }
        };
    }
}
