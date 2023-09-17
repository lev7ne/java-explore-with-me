package ru.practicum.ewm.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ParticipationRequestDto {
    private LocalDateTime created; // yyyy-MM-dd HH:mm:ss
    private Long event;
    private Long id;
    private Long requesterId;
    private String status;
}
