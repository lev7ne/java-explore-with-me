package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryCreateDto;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryUpdateDto;

public interface CategoryAdminService {
    CategoryDto create(CategoryCreateDto dto);

    CategoryDto update(CategoryUpdateDto dto, long id);

    void delete(long id);

}
