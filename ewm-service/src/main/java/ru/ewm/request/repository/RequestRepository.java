package ru.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ewm.request.model.Request;

import java.util.List;
import java.util.Optional;


public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

    @Query("SELECT r " +
            "FROM Request r " +
            "WHERE r.event.id = :eventId " +
            "AND r.event.initiator.id = :initiatorId ")
    List<Request> findByEventIdAndEventInitiatorId(Long eventId, Long initiatorId);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findRequestByIdIn(List<Long> requestIds);

    long countByEventIdAndRequestStatus(long eventId, Request.RequestStatus requestStatus);

//    long countByEvent_IdAndRequestStatus(long eventId, Request.RequestStatus requestStatus);

    List<Request> findAllByRequestStatusAndEventIdIn(Request.RequestStatus requestStatus, List<Long> eventIds);
}
