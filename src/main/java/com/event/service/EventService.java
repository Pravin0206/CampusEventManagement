package com.event.service;

import com.event.entity.Event;
import com.event.entity.Category;
import java.util.List;
import java.util.Optional;

public interface EventService {

    List<Event> getAllEvents();

    Optional<Event> getEventById(Long id);

    Event saveEvent(Event event);

    void deleteEvent(Long id);

    // Flexible search: keyword + optional category
    List<Event> searchEvents(String keyword, Long categoryId);

    // For populating categories dropdown
    List<Category> getAllCategories();
}