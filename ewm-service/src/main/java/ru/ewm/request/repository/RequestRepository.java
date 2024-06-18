package ru.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ewm.request.model.Request;

import java.util.List;
import java.util.Optional;


public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

//    List<Request> getAllByRequester_IdAndEvent_Id(Long requesterId, Long eventId);
//
//    Optional<Request> getByRequesterIdAndEventId(long requesterId, long eventId);
//
//    List<Request> getAllByEvent_Id(Long eventId);

    @Query("select r from Request r " +
            " where r.event.id = :eventId " +
            " and r.event.initiator.id = :initiatorId ")
    List<Request> findByEventIdAndEventInitiatorId(Long eventId, Long initiatorId);


    List<Request> findAllByRequesterId(Long requesterId);

//    List<Request> getAllByRequester_Id(Long requesterId);

//    List<Request> getEventRequestsByIdIn(List<Long> requestIds);

    List<Request> findRequestByIdIn(List<Long> requestIds);

    long countByEventIdAndRequestStatus(long eventId, Request.RequestStatus requestStatus);

    long countByEvent_IdAndRequestStatus(long eventId, Request.RequestStatus requestStatus);

    List<Request> getAllByRequestStatusAndEvent_IdIn(Request.RequestStatus requestStatus, List<Long> eventIds);
}
