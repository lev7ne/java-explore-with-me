package ru.ewm.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class EndpointHitDto {
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
