package ru.practicum.ewm.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

@UtilityClass
public class CategoryMapper {
    public Category toCategoryFromCategoryDto(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public NewCategoryDto toCategoryDtoFromCategory(Category category) {
        return NewCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
