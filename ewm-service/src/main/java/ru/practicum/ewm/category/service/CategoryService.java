package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    NewCategoryDto save(NewCategoryDto newCategoryDto);

    NewCategoryDto update(NewCategoryDto newCategoryDto, Long catId);

    void deleteById(Long catId);

    List<NewCategoryDto> findAll(Integer from, Integer size);

    NewCategoryDto findById(Long catId);
}
