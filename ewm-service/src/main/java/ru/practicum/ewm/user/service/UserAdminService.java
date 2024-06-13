package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.UserCreateDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserAdminService {
    UserDto create(UserCreateDto newUserRequest);

    void delete(long id);

    List<UserDto> index(List<Long> ids, Pageable pageable);
}
