package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.util.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryPublicServiceImpl implements CategoryPublicService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Endpoint: GET "/categories"
     *
     * @param pageable
     * @return List<CategoryDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> index(Pageable pageable) {
        var categories = categoryRepository.findAll(pageable).getContent();

        if (categories.isEmpty()) {
            return List.of();
        }

        var dtos = categories.stream()
                .map(categoryMapper::map)
                .toList();

        return dtos;
    }

    /**
     * Чтение категории по id;
     *
     * @param id
     * @return CategoryDto
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryDto show(long id) {
        var category = categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Category with id=" + id + " was not found"));

        return categoryMapper.map(category);
    }
}
