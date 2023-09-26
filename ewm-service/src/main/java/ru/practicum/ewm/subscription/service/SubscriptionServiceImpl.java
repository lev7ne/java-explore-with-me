package ru.practicum.ewm.subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.subscription.repository.SubscriptionRepository;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.exception.InvalidRequestException;
import ru.practicum.ewm.util.helper.ObjectFinder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    /**
     * Endpoint: POST "/users/{userId}/subscribers/{subscriberId}"
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
            throw new InvalidRequestException("User is already subscribed");
        }

        Subscription newSubscription = Subscription
                .builder()
                .userId(userId)
                .subscribedUserId(subscribedUserId)
                .build();

        Subscription checkMutualSubscription = subscriptionRepository.getByUserIdAndSubscribedUserId(subscribedUserId, userId);
        if (checkMutualSubscription != null) {
            newSubscription.setMutualSubscription(true);
            Subscription anySubscription = ObjectFinder.findSubscriptionById(subscriptionRepository, checkMutualSubscription.getId());
            anySubscription.setMutualSubscription(true);
            subscriptionRepository.save(anySubscription);
        }
        subscriptionRepository.save(newSubscription);

        return getUserShortDtos(userId);
    }

    /**
     * Endpoint: DELETE "/users/{userId}/subscribers/{subscriberId}"
     *
     * @param userId
     * @param subscriberId
     * @return
     */
    @Override
    @Transactional
    public List<UserShortDto> unsubscribe(Long userId, Long subscriberId) {
        ObjectFinder.findUserById(userRepository, userId);
        ObjectFinder.findUserById(userRepository, subscriberId);

        if (subscriptionRepository.existsByUserIdAndSubscribedUserId(userId, subscriberId)) {
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
     * Endpoint: GET "/users/{userId}/subscribers/all"
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserShortDto> getAll(Long userId, Pageable pageable) {
        List<Subscription> subscriptions = subscriptionRepository.getAllByUserId(userId);
        List<Long> subscriptionsIds = subscriptions.stream()
                .map(Subscription::getUserId)
                .collect(Collectors.toList());

        return userRepository.getUsersByIdIn(subscriptionsIds, pageable).stream()
                .map(UserMapper::toUserShortDtoFromUser)
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
