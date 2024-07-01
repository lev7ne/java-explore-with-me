package ru.ewm.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ewm.category.model.Category;


public interface CategoryRepository extends JpaRepository<Category, Long> {
}
