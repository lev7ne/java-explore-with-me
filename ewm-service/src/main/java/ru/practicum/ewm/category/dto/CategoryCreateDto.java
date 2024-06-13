package ru.practicum.ewm.category.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CategoryCreateDto {
    @NotBlank
    @Length(min = 1, max = 50)
    private String name;
}
