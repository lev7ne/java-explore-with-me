package ru.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.ewm.user.dto.UserCreateDto;
import ru.ewm.user.dto.UserDto;

import java.util.List;


public interface UserAdminService {
    UserDto create(UserCreateDto createDto);

    void delete(long id);

    List<UserDto> index(List<Long> ids, Pageable pageable);
}
