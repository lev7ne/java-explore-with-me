package ru.practicum.ewm.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.helper.ObjectHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto save(NewUserRequest newUserRequest) {
        return UserMapper.toUserDtoFromUser(userRepository.save(UserMapper.toUserFromUserDtoRequest(newUserRequest)));
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        ObjectHelper.findUserById(userRepository, userId);
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findByIds(List<Long> ids, Integer from, Integer size) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
//        PageRequest page = ObjectHelper.getPageRequest(from, size);
        return userRepository.getUsersByIdIn(ids, PageRequest.of(from, size)).stream()
                .map(UserMapper::toUserDtoFromUser)
                .collect(Collectors.toList());
    }
}
