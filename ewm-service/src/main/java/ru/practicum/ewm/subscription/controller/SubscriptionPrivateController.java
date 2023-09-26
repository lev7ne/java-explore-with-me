package ru.practicum.ewm.subscription.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.subscription.service.SubscriptionService;
import ru.practicum.ewm.user.dto.UserShortDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}")
@Validated
@RequiredArgsConstructor
public class SubscriptionPrivateController {
    private final SubscriptionService subscriptionService;

    /**
     * Пользователь с userId подписывается на пользователя subscribedUserId,
     * возвращает список всех пользователей на которых подписан subscriberId, включая нового "отслеживаемого" пользователя userId
     *
     * @param userId
     * @param subscribedUserId
     * @return List<UserShortDto>
     */
    @PostMapping(path = "/subscriptions/{subscribedUserId}")
    @ResponseStatus(HttpStatus.CREATED)
    public List<UserShortDto> subscribe(@PathVariable Long userId,
                                        @PathVariable Long subscribedUserId) {
        return subscriptionService.subscribe(userId, subscribedUserId);
    }

    /**
     * Пользователь с subscriberId отписывается от пользователя userId
     *
     * @param userId
     * @param subscriberId
     * @return List<UserShortDto>
     */
    @DeleteMapping(value = "/subscriptions/{subscriberId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserShortDto> unsubscribe(@PathVariable Long userId,
                                          @PathVariable Long subscriberId) {
        return subscriptionService.unsubscribe(userId, subscriberId);
    }

    /**
     * Возвращает всех пользователей на которых подписан пользователь с userId
     *
     * @param userId
     * @param from
     * @param size
     * @return List<UserShortDto>
     */
    @GetMapping(value = "/subscriptions/all")
    @ResponseStatus(HttpStatus.OK)
    public List<UserShortDto> getAll(@PathVariable Long userId,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return subscriptionService.getAll(userId, pageable);
    }

    /**
     * Пользователь с userId отменяет подписки на всех пользователей
     *
     * @param userId
     */
    @DeleteMapping(value = "/subscriptions/unsubscribe/all")
    @ResponseStatus(HttpStatus.OK)
    public void unfollowEveryone(@PathVariable Long userId) {
        subscriptionService.unfollowEveryone(userId);
    }

}
