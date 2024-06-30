package ru.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
public class CompilationCreateDto {
    @NotBlank(message = "The compilation title cannot be empty")
    @Size(min = 1, max = 50)
    private String title;
    private boolean pinned;
    private List<Long> events = new ArrayList<>();
}
