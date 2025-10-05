package com.event.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories") // matches your table name
public class Category
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false, unique = true)
    private String name;

    // Optional: events in this category
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Event> events = new HashSet<>();

    // Constructors
    public Category()
    {
    	
    }

    public Category(String name)
    {
        this.name = name;
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

    public Set<Event> getEvents()
    { 
    	return events;
    }
    
    public void setEvents(Set<Event> events)
    { 
    	this.events = events;
    }
}
