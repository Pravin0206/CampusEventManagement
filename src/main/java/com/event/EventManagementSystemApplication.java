package com.event;

import com.event.entity.User;
import com.event.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class EventManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventManagementSystemApplication.class, args);
    }

    
    // CommandLineRunner to update passwords for admin and student
    @Bean
    CommandLineRunner run(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            String adminPassword = "admin";    // plain password for admin
            String studentPassword = "1122";   // plain password for student

            // Update all admin users
            List<User> admins = userRepository.findByRole("admin");
            for (User admin : admins) {
                admin.setPassword(encoder.encode(adminPassword));
                userRepository.save(admin);
                System.out.println("Updated password for admin: " + admin.getUsername());
            }

            // Update all student users
            List<User> students = userRepository.findByRole("student");
            for (User student : students) {
                student.setPassword(encoder.encode(studentPassword));
                userRepository.save(student);
                System.out.println("Updated password for student: " + student.getUsername());
            }

            System.out.println("Password update complete for all users!");
        };
    }
}