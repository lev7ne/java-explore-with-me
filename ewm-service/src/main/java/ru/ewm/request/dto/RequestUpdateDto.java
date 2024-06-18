package ru.ewm.request.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class RequestUpdateDto {
    private List<Long> requestIds;
    private RequestStatus status;

    public enum RequestStatus {
        CONFIRMED, REJECTED
    }
}
