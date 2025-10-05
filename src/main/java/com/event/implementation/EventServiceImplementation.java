package com.event.implementation;

import com.event.entity.Event;
import com.event.entity.Category;
import com.event.repository.EventRepository;
import com.event.repository.CategoryRepository;
import com.event.service.EventService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImplementation implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository; // Add this

    public EventServiceImplementation(EventRepository eventRepository, CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository; // initialize
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<Event> searchEvents(String keyword, Long categoryId) {
        if ((keyword == null || keyword.isEmpty()) && categoryId == null) {
            return getAllEvents();
        } else if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            return eventRepository.findByTitleContainingIgnoreCaseAndCategoryId(keyword, categoryId);
        } else if (keyword != null && !keyword.isEmpty()) {
            return eventRepository.findByTitleContainingIgnoreCase(keyword);
        } else {
            return eventRepository.findByCategoryId(categoryId);
        }
    }

    // 🔹 New: fetch all categories
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}