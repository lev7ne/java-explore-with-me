package ru.ewm.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.user.dto.UserCreateDto;
import ru.ewm.user.dto.UserDto;
import ru.ewm.user.service.UserAdminService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserAdminService userAdminService;

    @PostMapping("")
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto createDto) {
        var dto = userAdminService.create(createDto);

        log.info("Создание пользователя: createDto={}", createDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping("")
    public ResponseEntity<List<UserDto>> index(@RequestParam(required = false) List<Long> ids,
                                               @RequestParam(defaultValue = "0") @Min(0) int from,
                                               @RequestParam(defaultValue = "10") @Min(1) int size) {

        log.info("Получение всех пользователей из списка идентификаторов (с пагинацией):" +
                "ids={}, from={}, size={}", ids, from, size);

        var pageable = PageRequest.of(from / size, size);
        List<UserDto> dtos = userAdminService.index(ids, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {

        log.info("Удаление пользователя по идентификатору: id={}", id);

        userAdminService.delete(id);
    }
}
