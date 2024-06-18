package ru.ewm.category.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.category.dto.CategoryDto;
import ru.ewm.category.service.CategoryPublicService;

import java.util.List;


@RestController
@RequestMapping(value = "/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryPublicService categoryPublicService;

    @GetMapping("")
    public ResponseEntity<List<CategoryDto>> index(@RequestParam(defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(defaultValue = "10") @Min(1) int size) {

        var pageable = PageRequest.of(from / size, size);
        List<CategoryDto> dtos = categoryPublicService.index(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDto> show(@PathVariable long id) {
        var dto = categoryPublicService.show(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }
}
