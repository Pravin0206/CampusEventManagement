package com.event.implementation;

import com.event.entity.User;
import com.event.repository.UserRepository;
import com.event.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByEmailIgnoreCase(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        // 🔹 Store plain password (NO encoding)
        user.setRole(user.getRole().toLowerCase()); // normalize role
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateUserProfile(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            // Update editable fields
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setUsername(updatedUser.getUsername());

            // 🔹 Only update password if not blank
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(updatedUser.getPassword()); // plain text
                // If using BCrypt: user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            // Normalize role if provided
            if (updatedUser.getRole() != null) {
                user.setRole(updatedUser.getRole().toLowerCase());
            }

            // Update additional fields
            user.setPhone(updatedUser.getPhone());
            user.setLinkedIn(updatedUser.getLinkedIn());
            user.setBio(updatedUser.getBio());

            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User login(String username, String password, String role) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // ✅ Compare plain text
            if (user.getPassword().equals(password) &&
                user.getRole().equalsIgnoreCase(role)) {
                return user;
            }
        }
        return null;
    }
}