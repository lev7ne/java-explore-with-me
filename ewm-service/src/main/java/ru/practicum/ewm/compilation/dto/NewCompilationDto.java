package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private Long id;
    @NotBlank(message = "The compilation title cannot be empty")
    @Length(min = 1, max = 50)
    private String title;
    private Boolean pinned = false;
    private List<Long> events = new ArrayList<>();
}
