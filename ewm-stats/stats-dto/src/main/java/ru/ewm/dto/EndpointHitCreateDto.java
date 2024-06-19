package ru.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
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
