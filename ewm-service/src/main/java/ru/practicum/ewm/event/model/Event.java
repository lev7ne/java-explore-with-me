package ru.practicum.ewm.event.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
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
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;
    private LocalDateTime eventDate;
    private double lat;
    private double lon;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    @CreatedDate
    private LocalDateTime createDate;
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
