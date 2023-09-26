package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserAdminService {
    UserDto create(NewUserRequest newUserRequest);

    void delete(Long userId);

    List<UserDto> getAllByIds(List<Long> ids, Pageable pageable);
}
