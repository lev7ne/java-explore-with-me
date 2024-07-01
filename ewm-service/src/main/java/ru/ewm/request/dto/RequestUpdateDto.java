package ru.ewm.request.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
public class RequestUpdateDto {
    private List<Long> requestIds;
    private RequestStatus status;

    public enum RequestStatus {
        CONFIRMED, REJECTED
    }
}
