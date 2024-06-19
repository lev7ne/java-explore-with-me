package ru.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.category.dto.CategoryDto;
import ru.ewm.category.mapper.CategoryMapper;
import ru.ewm.category.model.Category;
import ru.ewm.category.repository.CategoryRepository;
import ru.ewm.util.exception.NotFoundException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryPublicServiceImpl implements CategoryPublicService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Возвращает информацию обо всех категориях с параметром ограничения выборки.
     * В случае, если не найдено ни одной категории, возвращает пустой список.
     *
     * @param pageable - параметры ограничения выборки
     * @return List<CategoryDto> - список DTO возвращаемых пользователю
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> index(Pageable pageable) {
        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        if (categories.isEmpty()) {
            return List.of();
        }

        List<CategoryDto> dtos = categories.stream()
                .map(categoryMapper::map)
                .toList();

        return dtos;
    }

    /**
     * Возвращает конкретную категорию по идентификатору (id).
     *
     * @param id - указанные идентификаторы пользователей
     * @return CategoryDto - DTO возвращаемый пользователю
     * @throws NotFoundException если не удалось найти категорию по полученному идентификатору
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryDto show(long id) {
        var category = categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Category with id=" + id + " was not found"));

        return categoryMapper.map(category);
    }
}
