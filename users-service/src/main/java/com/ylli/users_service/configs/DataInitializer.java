package com.ylli.users_service.configs;

import com.ylli.shared.enums.UserRole;
import com.ylli.shared.models.User;
import com.ylli.users_service.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User defaultUser = new User();
            defaultUser.setUsername("admin");
            defaultUser.setPassword("Baba123@"); 
            defaultUser.setRoles(Set.of(UserRole.ROLE_ADMIN));
            userRepository.save(defaultUser);
            System.out.println("Default admin user created.");
        }
    }
}
