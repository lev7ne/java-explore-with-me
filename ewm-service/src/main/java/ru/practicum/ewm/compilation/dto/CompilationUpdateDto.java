package ru.practicum.ewm.compilation.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CompilationUpdateDto {
    private Long id;
    @Length(min = 1, max = 50)
    private String title;
    private Boolean pinned;
    private List<Long> events = new ArrayList<>();
}
