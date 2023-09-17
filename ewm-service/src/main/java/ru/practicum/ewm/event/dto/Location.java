package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {
    private Double lat;
    private Double lon;
}
