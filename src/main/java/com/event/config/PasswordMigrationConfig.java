package com.event.config;

import com.event.entity.User;
import com.event.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class PasswordMigrationConfig {

	@Bean
	public CommandLineRunner ensurePasswordsEncoded(UserRepository userRepository, PasswordEncoder passwordEncoder) {
	    return args -> {
	        List<User> users = userRepository.findAll();
	        for (User user : users) {
	            String pwd = user.getPassword();
	            
	            // If password looks like plain text (not a bcrypt hash)
	            if (!(pwd.startsWith("$2a$") || pwd.startsWith("$2b$") || pwd.startsWith("$2y$"))) {
	                user.setPassword(passwordEncoder.encode(pwd));
	                userRepository.save(user);
	                System.out.println("Re-encoded plain password for user: " + user.getUsername());
	            }
	        }
	    };
	}
}