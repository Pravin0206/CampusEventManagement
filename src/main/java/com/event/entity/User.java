package com.event.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "account")
public class User
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80, nullable = false)
    private String name;

    @Column(length = 80, nullable = false, unique = true)
    private String email;

    @Column(length = 80, nullable = false, unique = true)
    private String username;

    @Column(length = 80, nullable = false)
    private String password;

    @Column(length = 80, nullable = false)
    private String role;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 255)
    private String linkedIn;
    
    @Column(length = 1000)
    private String bio;

    // Constructors
    public User()
    {
    	
    }

    public User(String name, String email, String username, String password, String role)
    {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role.toLowerCase();
    }

    // Getters and Setters
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role.toLowerCase();
    }
    
    public String getPhone()
    { 
    	return phone;
    }
    
    public void setPhone(String phone)
    { 
    	this.phone = phone;
    }

    public String getLinkedIn()
    { 
    	return linkedIn;
    }
    
    public void setLinkedIn(String linkedIn)
    { 
    	this.linkedIn = linkedIn;
    }

    public String getBio()
    { 
    	return bio;
    }
    
    public void setBio(String bio)
    { 
    	this.bio = bio;
    }
}