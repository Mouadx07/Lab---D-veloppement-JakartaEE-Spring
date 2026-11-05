package ma.fstg.security.config;

import ma.fstg.security.entities.Role;
import ma.fstg.security.entities.User;
import ma.fstg.security.repositories.RoleRepository;
import ma.fstg.security.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DatabaseInitializer {

    @Bean
    CommandLineRunner init(RoleRepository roleRepo, UserRepository userRepo, BCryptPasswordEncoder encoder) {
        return args -> {
            // Only create what doesn't exist yet, so restarts don't insert duplicates
            Role adminRole = findOrCreateRole(roleRepo, "ROLE_ADMIN");
            Role userRole = findOrCreateRole(roleRepo, "ROLE_USER");

            createUserIfMissing(userRepo, encoder, "admin", "1234", List.of(adminRole, userRole));
            createUserIfMissing(userRepo, encoder, "user", "1111", List.of(userRole));
        };
    }

    private Role findOrCreateRole(RoleRepository roleRepo, String name) {
        Role role = roleRepo.findByName(name);
        return role != null ? role : roleRepo.save(new Role(null, name));
    }

    private void createUserIfMissing(UserRepository userRepo, BCryptPasswordEncoder encoder,
                                     String username, String rawPassword, List<Role> roles) {
        if (userRepo.findByUsername(username).isEmpty()) {
            userRepo.save(new User(null, username, encoder.encode(rawPassword), true, new ArrayList<>(roles)));
        }
    }
}
