package ru.ewm.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.ewm.request.model.Request;

import java.time.LocalDateTime;


@Getter
@Setter
public class ParticipationRequestDto {
    private long id;
    private LocalDateTime created;
    private long event;
    private long requester;
    private Request.RequestStatus status;
}
