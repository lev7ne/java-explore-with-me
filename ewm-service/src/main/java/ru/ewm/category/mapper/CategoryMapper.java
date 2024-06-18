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
    public abstract Category map(CategoryCreateDto dto);

    public abstract CategoryDto map(Category model);

    public abstract void update(CategoryUpdateDto dto, @MappingTarget Category model);
}
