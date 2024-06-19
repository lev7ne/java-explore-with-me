package ru.ewm.event.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.ewm.event.dto.EventParamDto;
import ru.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class EventSpecification {
    public Specification<Event> build(EventParamDto params) {
        return withTextInAnnotationOrDescription(params.getText())
                .and(withInitiatorId(params.getUsers()))
                .and(withCategories(params.getCategories()))
                .and(withPaid(params.isPaid()))
                .and(withRangeStartGt(params.getRangeStart()))
                .and(withRangeEndLt(params.getRangeEnd()))
                .and(withEventState(params.getStates()));
    }

    private static Specification<Event> withTextInAnnotationOrDescription(String text) {
        return (root, query, cb) -> {
            if (text == null || text.trim().isEmpty()) {
                return cb.conjunction();
            }
            String likePattern = "%" + text.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("annotation")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern)
            );
        };
    }

    private Specification<Event> withInitiatorId(List<Long> ids) {
        return (root, query, cb) -> ids == null || ids.isEmpty() ? cb.conjunction() :
                root.get("initiator").get("id").in(ids);
    }

    private Specification<Event> withCategories(List<Long> categories) {
        return (root, query, cb) -> categories == null || categories.isEmpty() ? cb.conjunction() :
                root.get("category").get("id").in(categories);
    }

    private Specification<Event> withPaid(Boolean paid) {
        return (root, query, cb) -> paid == null ? cb.conjunction() :
                cb.equal(root.get("paid"), paid);
    }

    private Specification<Event> withRangeStartGt(LocalDateTime rangeStartGt) {
        return (root, query, cb) -> rangeStartGt == null ? cb.conjunction() :
                cb.greaterThan(root.get("eventDate"), rangeStartGt);
    }

    private Specification<Event> withRangeEndLt(LocalDateTime rangeEndLt) {
        return (root, query, cb) -> rangeEndLt == null ? cb.conjunction() :
                cb.lessThan(root.get("eventDate"), rangeEndLt);
    }


    private Specification<Event> withEventState(List<Event.State> states) {
        return (root, query, cb) -> states == null || states.isEmpty() ? cb.conjunction() :
                root.get("state").in(states);
    }
}
