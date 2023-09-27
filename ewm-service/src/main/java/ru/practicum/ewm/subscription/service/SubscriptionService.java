package ru.practicum.ewm.subscription.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.UserDtoWithEvents;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.List;

public interface SubscriptionService {
    List<UserShortDto> subscribe(Long userId, Long subscriberId);

    List<UserShortDto> unsubscribe(Long userId, Long subscriberId);

    List<UserDtoWithEvents> getAll(Long userId, Pageable pageable);

    void unfollowEveryone(Long userId);
}
