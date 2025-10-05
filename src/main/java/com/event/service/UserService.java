package com.event.service;

import com.event.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
	List<User> getAllUsers();
	
    Optional<User> getUserById(Long id);
    
    Optional<User> getUserByUsername(String username);
    
    Optional<User> getUserByEmail(String email);
    
    User saveUser(User user);
    
    void deleteUser(Long id);

    // Rename this to match implementation
    User updateUserProfile(Long id, User user);

    User login(String username, String password, String role);
    
    Optional<User> getUserByEmailIgnoreCase(String email);
}