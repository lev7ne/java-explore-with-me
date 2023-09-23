package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.util.helper.ObjectFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryPublicServiceImpl implements CategoryPublicService {
    private final CategoryRepository categoryRepository;

    /**
     * Endpoint: GET "/categories"
     *
     * @param pageable
     * @return List<CategoryDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);

        if (categories.getContent().isEmpty()) {
            return new ArrayList<>();
        }

        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDtoFromCategory)
                .collect(Collectors.toList());
    }

    /**
     * Endpoint: GET "/categories/{catId}"
     *
     * @param catId
     * @return CategoryDto
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long catId) {
        return CategoryMapper.toCategoryDtoFromCategory(
                ObjectFinder.findCategoryById(categoryRepository, catId)
        );
    }
}
