package ru.practicum.ewm.compilation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Event;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "pinned")
    private Boolean pinned;
    @ManyToMany
    @JoinTable(
            name = "event_compilation", // Имя объединяемой таблицы
            joinColumns = @JoinColumn(name = "event_id"), // Столбец внешнего ключа в объединяемой таблице, ссылающийся на таблицу "events"
            inverseJoinColumns = @JoinColumn(name = "compilation_id") // Столбец внешнего ключа в объединяемой таблице, ссылающийся на таблицу "compilations"
    )
    private List<Event> events;
}
