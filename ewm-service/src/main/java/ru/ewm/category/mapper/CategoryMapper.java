package ru.ewm.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.ewm.category.dto.CategoryCreateDto;
import ru.ewm.category.dto.CategoryDto;
import ru.ewm.category.dto.CategoryUpdateDto;
import ru.ewm.category.model.Category;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class CategoryMapper {
    public abstract Category toEntity(CategoryCreateDto dto);

    public abstract CategoryDto toDto(Category model);

    public abstract void update(CategoryUpdateDto dto, @MappingTarget Category model);
}
