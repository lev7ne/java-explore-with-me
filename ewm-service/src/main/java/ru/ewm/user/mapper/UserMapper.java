package ru.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.ewm.user.dto.UserCreateDto;
import ru.ewm.user.dto.UserDto;
import ru.ewm.user.dto.UserShortDto;
import ru.ewm.user.model.User;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class UserMapper {
    public abstract User map(UserCreateDto dto);

    public abstract UserDto map(User model);

    public abstract UserShortDto mapShort(User model);
}
