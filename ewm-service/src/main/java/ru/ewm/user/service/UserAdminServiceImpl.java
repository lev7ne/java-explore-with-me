package ru.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.user.dto.UserCreateDto;
import ru.ewm.user.dto.UserDto;
import ru.ewm.user.mapper.UserMapper;
import ru.ewm.user.model.User;
import ru.ewm.user.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Создание и добавление нового пользователя в репозиторий (БД).
     *
     * @param createDto (DTO для создания нового пользователя)
     * @return UserDto (DTO возвращаемый пользователю)
     */
    @Override
    @Transactional
    public UserDto create(UserCreateDto createDto) {
        var user = userMapper.map(createDto);
        user = userRepository.save(user);

        return userMapper.map(user);
    }

    /**
     * Удаление пользователя из репозитория (БД).
     *
     * @param id (идентификатор пользователя)
     */
    @Override
    @Transactional
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    /**
     * Возвращает информацию обо всех пользователях, либо о конкретных (ids).
     * В случае, если по заданным фильтрам не найдено ни одного пользователя, возвращает пустой список.
     *
     * @param ids      (указанные идентификаторы пользователей)
     * @param pageable (параметры ограничения выборки)
     * @return List<UserDto> (список DTO возвращаемых пользователю)
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> index(List<Long> ids, Pageable pageable) {
        List<User> users;

        if (ids == null) {
            users = userRepository.findAll(pageable).getContent();
        } else {
            users = userRepository.getUsersByIdIn(ids, pageable);
        }

        List<UserDto> dtos = users.stream()
                .map(userMapper::map)
                .toList();

        return dtos;
    }
}
