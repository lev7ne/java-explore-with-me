package ru.ewm.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    @Mapping(target = "id", ignore = true)
    public abstract Category toEntity(CategoryCreateDto dto);

    @Mapping(target = "id", source = "model.id")
    public abstract CategoryDto toDto(Category model);

    @Mapping(target = "id", ignore = true)
    public abstract void update(CategoryUpdateDto dto, @MappingTarget Category model);
}
