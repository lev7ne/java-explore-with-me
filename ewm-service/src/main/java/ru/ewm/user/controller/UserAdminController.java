package ru.ewm.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.user.dto.UserCreateDto;
import ru.ewm.user.dto.UserDto;
import ru.ewm.user.service.UserAdminService;

import java.util.List;


@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserAdminService userAdminService;

    @PostMapping("")
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto createDto) {
        var dto = userAdminService.create(createDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping("")
    public ResponseEntity<List<UserDto>> index(@RequestParam(required = false) List<Long> ids,
                                               @RequestParam(defaultValue = "0") @Min(0) int from,
                                               @RequestParam(defaultValue = "10") @Min(1) int size) {

        var pageable = PageRequest.of(from / size, size);
        List<UserDto> dtos = userAdminService.index(ids, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        userAdminService.delete(id);
    }
}
