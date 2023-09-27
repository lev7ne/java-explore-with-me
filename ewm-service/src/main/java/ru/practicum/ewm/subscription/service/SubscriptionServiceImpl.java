package ru.practicum.ewm.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.subscription.repository.SubscriptionRepository;
import ru.practicum.ewm.user.dto.UserDtoWithEvents;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.exception.ConditionMismatchException;
import ru.practicum.ewm.util.exception.InvalidRequestException;
import ru.practicum.ewm.util.helper.ObjectCounter;
import ru.practicum.ewm.util.helper.ObjectFinder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    /**
     * Endpoint: POST "/users/{userId}/subscribers/{subscribedUserId}"
     *
     * @param userId
     * @param subscribedUserId
     * @return List<UserShortDto>
     */
    @Override
    @Transactional
    public List<UserShortDto> subscribe(Long userId, Long subscribedUserId) {
        ObjectFinder.findUserById(userRepository, userId);
        ObjectFinder.findUserById(userRepository, subscribedUserId);

        if (subscriptionRepository.existsByUserIdAndSubscribedUserId(userId, subscribedUserId)) {
            throw new ConditionMismatchException("User is already subscribed");
        }

        Subscription newSubscription = Subscription
                .builder()
                .userId(userId)
                .subscribedUserId(subscribedUserId)
                .build();

        Subscription checkMutualSubscription = subscriptionRepository.getByUserIdAndSubscribedUserId(subscribedUserId, userId);
        if (checkMutualSubscription != null && !checkMutualSubscription.isMutualSubscription()) {
            newSubscription.setMutualSubscription(true);
            Subscription anySubscription = ObjectFinder.findSubscriptionById(subscriptionRepository, checkMutualSubscription.getId());
            anySubscription.setMutualSubscription(true);
            subscriptionRepository.save(anySubscription);
        }
        subscriptionRepository.save(newSubscription);

        return getUserShortDtos(userId);
    }

    /**
     * Endpoint: DELETE "/users/{userId}/subscribers/{subscribedUserId}"
     *
     * @param userId
     * @param subscriberId
     * @return List<UserShortDto>
     */
    @Override
    @Transactional
    public List<UserShortDto> unsubscribe(Long userId, Long subscriberId) {
        ObjectFinder.findUserById(userRepository, userId);
        ObjectFinder.findUserById(userRepository, subscriberId);

        if (!subscriptionRepository.existsByUserIdAndSubscribedUserId(userId, subscriberId)) {
            throw new InvalidRequestException("User is not subscribed");
        }

        Subscription checkMutualSubscription = subscriptionRepository.getByUserIdAndSubscribedUserId(subscriberId, userId);
        if (checkMutualSubscription != null) {
            Subscription anySubscription = ObjectFinder.findSubscriptionById(subscriptionRepository, checkMutualSubscription.getId());
            anySubscription.setMutualSubscription(false);
            subscriptionRepository.save(anySubscription);
        }

        subscriptionRepository.deleteByUserIdAndAndSubscribedUserId(userId, subscriberId);

        return getUserShortDtos(userId);
    }

    /**
     * Endpoint: GET "/users/{userId}/subscriptions"
     *
     * @param userId
     * @return List<UserDtoWithEvents>
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDtoWithEvents> getAll(Long userId, Pageable pageable) {
        List<Subscription> subscriptions = subscriptionRepository.getAllByUserIdAndMutualSubscription(userId, true);
        List<Long> mutualSubscriptionsIds = subscriptions.stream()
                .map(Subscription::getSubscribedUserId)
                .collect(Collectors.toList());
        List<User> users = userRepository.getUsersByIdIn(mutualSubscriptionsIds, Pageable.unpaged());

        List<Event> mutualSubscriptionEvents = eventRepository.getEventsByInitiator_IdIn(mutualSubscriptionsIds);
        Map<Long, List<Event>> eventsGroupingByInitiatorId = mutualSubscriptionEvents.stream()
                .collect(Collectors.groupingBy(event -> event.getInitiator().getId()));
        List<Long> eventIds = mutualSubscriptionEvents.stream()
                .map(Event::getId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);
        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);

        return users.stream()
                .map(user -> {
                    UserDtoWithEvents userDtoWithEvents = UserDtoWithEvents.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .build();

                    List<Event> events = eventsGroupingByInitiatorId.get(user.getId());
                    events.forEach(event -> {
                        EventShortDto eventShortDto = EventMapper.toEventShortDtoFromEvent(event);
                        eventShortDto.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
                        eventShortDto.setViews(countViews.getOrDefault(event.getId(), 0L));

                        userDtoWithEvents.getPublishedSubEvents().add(eventShortDto);
                    });
                    return userDtoWithEvents;
                })
                .collect(Collectors.toList());
    }

    /**
     * helper method for collection List<UserShortDto>
     *
     * @param userId
     * @return List<UserShortDto>
     */
    private List<UserShortDto> getUserShortDtos(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.getAllByUserId(userId);
        List<Long> subscriptionsIds = subscriptions.stream()
                .map(Subscription::getUserId)
                .collect(Collectors.toList());

        return userRepository.getUsersByIdIn(subscriptionsIds, Pageable.unpaged()).stream()
                .map(UserMapper::toUserShortDtoFromUser)
                .collect(Collectors.toList());
    }

    /**
     * Endpoint: GET "/users/{userId}/unsubscribe/all"
     *
     * @param userId
     */
    @Override
    @Transactional
    public void unfollowEveryone(Long userId) {
        ObjectFinder.findUserById(userRepository, userId);
        subscriptionRepository.deleteAllByUserId(userId);

        List<Subscription> subscriptions = subscriptionRepository.getAllBySubscribedUserId(userId);
        List<Subscription> cancelSubscription = subscriptions.stream()
                .peek(subscription -> subscription.setMutualSubscription(false))
                .collect(Collectors.toList());

        subscriptionRepository.saveAll(cancelSubscription);
    }
}
