package ru.practicum.ewm.request.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus requestStatus;

    public enum RequestStatus {
        CONFIRMED, REJECTED
    }
}
