package com.event.repository;

import com.event.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username
    Optional<User> findByUsername(String username);

    // Find user by email (if you want)
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailIgnoreCase(String email);

    // Find users by role
    List<User> findByRole(String role);

    // Check if username exists
    boolean existsByUsername(String username);

    // Check if email exists
    boolean existsByEmail(String email);
    
    // New method for reports
    long countByRole(String role);
}