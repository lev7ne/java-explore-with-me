package ru.practicum.ewm.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.subscription.model.Subscription;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription getByUserIdAndSubscribedUserId(Long userId, Long subscribedUserId);

    Boolean existsByUserIdAndSubscribedUserId(Long userId, Long subscribedUserId);

    void deleteByUserIdAndAndSubscribedUserId(Long userId, Long subscribedUserId);

    List<Subscription> getAllByUserIdAndMutualSubscription(Long anyId, Boolean mutualSubscription);

    List<Subscription> getAllByUserId(Long anyIdn);

    List<Subscription> getAllBySubscribedUserId(Long anyId);

    void deleteAllByUserId(Long userId);

    @Override
    <S extends Subscription> List<S> saveAll(Iterable<S> entities);
}
