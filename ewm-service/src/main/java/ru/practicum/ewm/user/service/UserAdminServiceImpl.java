package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.helper.ObjectFinder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {
    private final UserRepository userRepository;

    /**
     * Endpoint: POST "/admin/users"
     *
     * @param newUserRequest
     * @return UserDto
     */
    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        return UserMapper.toUserDtoFromUser(userRepository.save(UserMapper.toUserFromUserDtoRequest(newUserRequest)));
    }

    /**
     * Endpoint: DELETE "/admin/users"
     *
     * @param userId
     */
    @Override
    @Transactional
    public void delete(Long userId) {
        ObjectFinder.findUserById(userRepository, userId);
        userRepository.deleteById(userId);
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
    public List<UserDto> getAllByIds(List<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDtoFromUser)
                    .collect(Collectors.toList());
        }

        return userRepository.getUsersByIdIn(ids, pageable).stream()
                .map(UserMapper::toUserDtoFromUser)
                .collect(Collectors.toList());
    }
}
