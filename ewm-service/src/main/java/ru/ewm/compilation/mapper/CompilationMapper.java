package ru.ewm.compilation.mapper;

import org.mapstruct.*;
import ru.ewm.compilation.dto.CompilationCreateDto;
import ru.ewm.compilation.dto.CompilationDto;
import ru.ewm.compilation.dto.CompilationUpdateDto;
import ru.ewm.compilation.model.Compilation;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class CompilationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "pinned", defaultValue = "false")
    public abstract Compilation toEntity(CompilationCreateDto dto);

    @Mapping(target = "events", ignore = true)
    public abstract CompilationDto toDto(Compilation model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "events", ignore = true)
    public abstract void update(CompilationUpdateDto dto, @MappingTarget Compilation model);
}
