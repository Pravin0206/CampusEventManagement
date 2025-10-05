package com.event.repository;

import com.event.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>
{
    // Optional: add custom queries if needed
	long countDistinctByEventsIsNotEmpty();
}