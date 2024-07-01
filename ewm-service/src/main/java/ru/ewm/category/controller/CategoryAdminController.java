package ru.ewm.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.category.dto.CategoryCreateDto;
import ru.ewm.category.dto.CategoryDto;
import ru.ewm.category.dto.CategoryUpdateDto;
import ru.ewm.category.service.CategoryAdminService;

@Slf4j
@RestController
@RequestMapping(value = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryAdminService categoryAdminService;

    @PostMapping("")
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryCreateDto createDto) {

        log.info("Получен POST-запрос /admin/categories на создание категории: createDto={}", createDto);

        var dto = categoryAdminService.create(createDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<CategoryDto> update(@Valid @RequestBody CategoryUpdateDto updateDto,
                                              @PathVariable long id) {

        log.info("Получен PATCH-запрос /admin/categories/{id} на обновление категории: " +
                "id={}, createDto={}", id, updateDto);



        var dto = categoryAdminService.update(updateDto, id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {

        log.info("Получен DELETE-запрос /admin/categories/{id} на удаление категории: id={}", id);

        categoryAdminService.delete(id);
    }
}
