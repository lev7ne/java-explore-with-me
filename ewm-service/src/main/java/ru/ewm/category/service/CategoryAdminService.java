package ru.ewm.category.service;

import ru.ewm.category.dto.CategoryCreateDto;
import ru.ewm.category.dto.CategoryDto;
import ru.ewm.category.dto.CategoryUpdateDto;


public interface CategoryAdminService {
    CategoryDto create(CategoryCreateDto dto);

    CategoryDto update(CategoryUpdateDto dto, long id);

    void delete(long id);
}
