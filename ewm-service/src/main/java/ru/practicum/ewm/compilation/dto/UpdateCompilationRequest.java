package ru.practicum.ewm.compilation.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
public class UpdateCompilationRequest {
    private Long id;
    @Length(min = 1, max = 40)
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
