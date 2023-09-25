package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> getAllByRequester_IdAndEvent_Id(Long requesterId, Long eventId);

    List<Request> getAllByEvent_Id(Long eventId);

    @Query("select r from Request r " +
    " where r.event.id = :eventId " +
    " and r.event.initiator.id = :initiatorId ")
    List<Request> getByEventIdAndInitiatorId(Long eventId, Long initiatorId);

    List<Request> getAllByRequester_Id(Long requesterId);

    List<Request> getEventRequestsByIdIn(List<Long> requestIds);

    Long countByEvent_IdAndRequestStatus(Long eventId, Request.RequestStatus requestStatus);

    List<Request> getAllByRequestStatusAndEvent_IdIn(Request.RequestStatus requestStatus, List<Long> eventIds);

}
