package ru.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.ewm.user.dto.UserCreateDto;
import ru.ewm.user.dto.UserDto;
import ru.ewm.user.dto.UserShortDto;
import ru.ewm.user.model.User;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class UserMapper {
    @Mapping(target = "id", ignore = true)
    public abstract User toEntity(UserCreateDto dto);

    @Mapping(target = "id", source = "model.id")
    public abstract UserDto toDto(User model);

    @Mapping(target = "id", ignore = true)
    public abstract UserShortDto mapShort(User model);
}
