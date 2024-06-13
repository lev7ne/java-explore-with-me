package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.dto.UserCreateDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Создание пользователя;
     *
     * @param dto
     * @return UserDto
     */
    @Override
    @Transactional
    public UserDto create(UserCreateDto dto) {
        var user = userMapper.map(dto);
        user = userRepository.save(user);

        return userMapper.map(user);
    }

    /**
     * Удаление пользователя;
     *
     * @param id
     */
    @Override
    @Transactional
    public void delete(long id) {
        userRepository.deleteById(id);
    }


    /**
     * Endpoint: GET "/admin/users"
     *
     * @param ids
     * @param pageable
     * @return List<UserDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> index(List<Long> ids, Pageable pageable) {
        List<User> users;

        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable).getContent();
        } else {
            users = userRepository.getUsersByIdIn(ids, pageable);
        }

        var dtos = users.stream()
                .map(userMapper::map)
                .toList();

        return dtos;
    }

}
