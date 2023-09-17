package ru.practicum.ewm.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "requester_id")
    private Long requesterId;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Column(name = "request_status")
    private RequestStatus requestStatus;
}
