package ru.practicum.ewm.compilation.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
public class NewCompilationDto {
    private Long id;
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
