package ru.practicum.ewm.category.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class NewCategoryDto {
    private Long id;
    @NotBlank
    @Length(min = 1, max = 50)
    private String name;
}
