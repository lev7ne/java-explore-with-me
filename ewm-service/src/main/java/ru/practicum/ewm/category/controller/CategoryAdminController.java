package ru.practicum.ewm.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewCategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.save(newCategoryDto);
    }

    @PatchMapping(value = "/{catId}")
    public NewCategoryDto update(@Valid @RequestBody NewCategoryDto newCategoryDto,
                                 @PathVariable Long catId) {
        return categoryService.update(newCategoryDto, catId);
    }

    @DeleteMapping(value = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long catId) {
        categoryService.deleteById(catId);
    }
}
