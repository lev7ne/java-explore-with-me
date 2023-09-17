package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(NewUserRequest newUserRequest);

    void deleteById(Long userId);

    List<UserDto> findByIds(List<Long> ids, Integer from, Integer size);
}
