package ru.ewm.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.ewm.compilation.dto.CompilationCreateDto;
import ru.ewm.compilation.dto.CompilationDto;
import ru.ewm.compilation.dto.CompilationUpdateDto;
import ru.ewm.compilation.model.Compilation;
import ru.ewm.event.mapper.EventMapper;


@Mapper(
        uses = EventMapper.class,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class CompilationMapper {
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "pinned", source = "pinned", defaultValue = "false")
    public abstract Compilation map(CompilationCreateDto dto);

    @Mapping(target = "events", ignore = true)
    public abstract CompilationDto map(Compilation model);

    @Mapping(target = "events", ignore = true)
    public abstract void update(CompilationUpdateDto dto, @MappingTarget Compilation model);
}
