package ru.practicum.ewm.subscription.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.subscription.service.SubscriptionService;
import ru.practicum.ewm.user.dto.UserDtoWithEvents;
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
     * возвращает список всех пользователей на которых подписан userId, включая нового "отслеживаемого" пользователя subscribedUserId
     *
     * @param userId основной пользователь, автор запроса
     * @param subscribedUserId пользователь на которого будет осуществлена подписка
     * @return List<UserShortDto>
     */
    @PostMapping(path = "/subscriptions/{subscribedUserId}")
    @ResponseStatus(HttpStatus.CREATED)
    public List<UserShortDto> subscribe(@PathVariable Long userId,
                                        @PathVariable Long subscribedUserId) {
        return subscriptionService.subscribe(userId, subscribedUserId);
    }

    /**
     * Пользователь с userId отписывается от пользователя subscribedUserId
     *
     * @param userId основной пользователь, автор запроса
     * @param subscribedUserId пользователь, подписка на которого будет отменена
     * @return List<UserShortDto>
     */
    @DeleteMapping(value = "/subscriptions/{subscribedUserId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserShortDto> unsubscribe(@PathVariable Long userId,
                                          @PathVariable Long subscribedUserId) {
        return subscriptionService.unsubscribe(userId, subscribedUserId);
    }

    /**
     * Возвращает всех пользователей для userId с взаимной подпиской, вместе с опубликованными ими событиями
     *
     * @param userId пользователь, для которого будут найдены все взаимные подписчики
     * @return List<UserDtoWithEvents>
     */
    @GetMapping(value = "/subscriptions")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDtoWithEvents> getAll(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return subscriptionService.getAll(userId, pageable);
    }

    /**
     * Пользователь с userId отменяет подписки на всех пользователей
     *
     * @param userId пользователь, который будет отписываться от всех других пользователей
     */
    @DeleteMapping(value = "/subscriptions/unsubscribe/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollowEveryone(@PathVariable Long userId) {
        subscriptionService.unfollowEveryone(userId);
    }
}
