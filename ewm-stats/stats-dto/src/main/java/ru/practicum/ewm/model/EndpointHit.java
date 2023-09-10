package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EndpointHit {
    private long id;
    @Getter
    private String app;
    @Getter
    private String uri;
    @Getter
    private String ip;
    @Getter
    private LocalDateTime timestamp;

}
