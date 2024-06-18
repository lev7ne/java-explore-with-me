package ru.ewm.compilation.model;

import jakarta.persistence.*;
import lombok.*;
import ru.ewm.event.model.Event;

import java.util.List;


@Entity
@Table(name = "compilations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private boolean pinned;
    @With
    @ManyToMany
    @JoinTable(name = "events_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;
}
