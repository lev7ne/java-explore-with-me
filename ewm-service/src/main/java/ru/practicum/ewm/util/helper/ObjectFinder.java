package ru.practicum.ewm.util.helper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.exception.NotFoundException;

@UtilityClass
public class ObjectFinder {
    public User findUserById(UserRepository repository, long userId) {
        return repository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    public Category findCategoryById(CategoryRepository repository, long catId) {
        return repository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id=" + catId + " was not found"));
    }

    public Event findEventById(EventRepository repository, long eventId) {
        return repository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    public Compilation findCompilationById(CompilationRepository repository, long compId) {
        return repository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + compId + " was not found"));
    }

    public Request findRequestById(RequestRepository repository, long requestId) {
        return repository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request with id=" + requestId + " was not found"));
    }
}
