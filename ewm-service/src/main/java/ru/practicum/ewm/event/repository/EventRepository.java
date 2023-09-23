package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> getEventsByInitiator_Id(Long userId, Pageable pageable);

    List<Event> getEventsByCategory_Id(Long catId);

    List<Event> getEventsByIdIn(List<Long> eventIds);

    @Query("SELECT e FROM Event e " +
            "WHERE ((:text IS NULL) OR (upper(e.annotation) LIKE upper(concat('%', :text, '%')) " +
            "OR upper(e.description) LIKE upper(concat('%', :text, '%')))) " +
            "AND ((:categories IS NULL) OR (e.category.id IN :categories)) " +
            "AND ((:paid IS NULL) OR (e.paid = :paid)) " +
            "AND ((CAST(:rangeStart AS date) IS NULL) OR (e.eventDate >= :rangeStart)) " +
            "AND ((CAST(:rangeEnd AS date) IS NULL) OR (e.eventDate <= :rangeEnd)) " +
            "AND e.state = 'PUBLISHED'")
    List<Event> getAllWithParam(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE ((:initiators IS NULL) OR (e.initiator.id IN :initiators)) " +
            "AND ((:states IS NULL) OR (e.state IN :states)) " +
            "AND ((:categories IS NULL) OR (e.category.id IN :categories)) " +
            "AND ((CAST(:rangeStart AS date) IS NULL) OR (e.eventDate >= :rangeStart)) " +
            "AND ((CAST(:rangeEnd AS date) IS NULL) OR (e.eventDate <= :rangeEnd)) ")
    List<Event> getAllForAdminWithParam(
            List<Long> initiators,
            List<Event.State> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable
    );
}
