package ru.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitCreateDto {
    @NotBlank
    @Size(max = 64)
    private String app;
    @NotBlank
    @Size(max = 2083)
    private String uri;
    @NotBlank
    @Size(max = 49)
    private String ip;
}
