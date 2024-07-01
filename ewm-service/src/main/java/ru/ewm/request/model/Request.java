package ru.ewm.request.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.ewm.event.model.Event;
import ru.ewm.user.model.User;

import java.time.LocalDateTime;


@Entity
@Table(name = "requests")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;
    @CreatedDate
    private LocalDateTime createDate;
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    public enum RequestStatus {
        PENDING, CONFIRMED, REJECTED, CANCELED
    }
}
