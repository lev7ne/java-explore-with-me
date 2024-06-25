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
}
