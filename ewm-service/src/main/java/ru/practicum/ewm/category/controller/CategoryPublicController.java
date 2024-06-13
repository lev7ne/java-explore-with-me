package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryPublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryPublicService categoryPublicService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CategoryDto>> index(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {

        var pageable = PageRequest.of(from / size, size);
        var dtos = categoryPublicService.index(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDto> show(@PathVariable Long id) {
        var dto = categoryPublicService.show(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }
}
