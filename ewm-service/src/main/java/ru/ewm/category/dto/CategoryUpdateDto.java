package ru.ewm.category.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class CategoryUpdateDto {
    @NotEmpty
    @Size(min = 1, max = 50)
    private String name;
}
