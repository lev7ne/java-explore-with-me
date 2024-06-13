package ru.practicum.ewm.util.helper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.subscription.repository.SubscriptionRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.exception.NotFoundException;

@UtilityClass
public class ObjectFinder {
    public User findUserById(UserRepository repository, long userId) {
        return repository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    public Subscription findSubscriptionById(SubscriptionRepository repository, long subsId) {
        return repository.findById(subsId).orElseThrow(() ->
                new NotFoundException("Request with id=" + subsId + " was not found"));
    }
}
