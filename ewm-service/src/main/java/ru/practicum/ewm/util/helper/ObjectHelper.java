package ru.practicum.ewm.util.helper;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.exception.ObjectNotFoundException;
import ru.practicum.ewm.util.exception.UnavailableException;

@UtilityClass
public class ObjectHelper {
    public User findUserById(UserRepository repository, long id) {
        return repository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь id: " + id + " не найден или ещё не создан."));
    }

    public Category findCategoryById(CategoryRepository repository, long id) {
        return repository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Категория id: " + id + " не найдена или ещё не создана."));
    }

    public PageRequest getPageRequest(int from, int size) {
        if (size > 0 && from >= 0) {
            int page = from / size;
            return PageRequest.of(page, size);
        } else {
            throw new UnavailableException("Некорректное значение from:" + from + " или size:" + size + ".");
        }
    }

}
