package ru.ewm.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.ewm.category.model.Category;
import ru.ewm.user.model.User;

import java.time.LocalDateTime;


@Entity
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;
    private LocalDateTime eventDate;
    private double lat;
    private double lon;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    @CreatedDate
    private LocalDateTime createdDate;
    private LocalDateTime publishedOn;
    @Enumerated(EnumType.STRING)
    private State state;

    public enum State {
        PENDING, PUBLISHED, CANCELED
    }

    public enum Sort {
        EVENT_DATE, VIEWS
    }
}
