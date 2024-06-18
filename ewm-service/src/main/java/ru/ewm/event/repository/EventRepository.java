package ru.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.ewm.event.model.Event;

import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);

    List<Event> findAllByCategoryId(long categoryId);

    List<Event> findByIdIn(List<Long> ids);

//    @Query("select e from Event e " +
//            "where e.initiator.id in :initiatorIds")
//    List<Event> findAllByInitiatorIdIn(List<Long> initiatorIds);

//    List<Event> findAllByIdIn(List<Long> ids);

//    @Query("SELECT e FROM Event e " +
//            "WHERE ((:text IS NULL) OR (upper(e.annotation) LIKE upper(concat('%', :text, '%')) " +
//            "OR upper(e.description) LIKE upper(concat('%', :text, '%')))) " +
//            "AND ((:categories IS NULL) OR (e.category.id IN :categories)) " +
//            "AND ((:paid IS NULL) OR (e.paid = :paid)) " +
//            "AND ((CAST(:rangeStart AS date) IS NULL) OR (e.eventDate >= :rangeStart)) " +
//            "AND ((CAST(:rangeEnd AS date) IS NULL) OR (e.eventDate <= :rangeEnd)) " +
//            "AND e.state = 'PUBLISHED'")
//    List<Event> getAllWithParam(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
//                                LocalDateTime rangeEnd, Pageable pageable);

//    @Query("SELECT e FROM Event e " +
//            "WHERE ((:initiators IS NULL) OR (e.initiator.id IN :initiatorIds)) " +
//            "AND ((:states IS NULL) OR (e.state IN :states)) " +
//            "AND ((:categories IS NULL) OR (e.category.id IN :categoryIds)) " +
//            "AND ((CAST(:rangeStart AS date) IS NULL) OR (e.eventDate >= :rangeStart)) " +
//            "AND ((CAST(:rangeEnd AS date) IS NULL) OR (e.eventDate <= :rangeEnd)) ")
//    List<Event> getAllForAdminWithParam(
//            List<Long> initiatorIds,
//            List<Event.State> states,
//            List<Long> categoryIds,
//            LocalDateTime rangeStart,
//            LocalDateTime rangeEnd,
//            Pageable pageable
//    );
}
