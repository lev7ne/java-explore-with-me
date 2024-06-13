package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryCreateDto;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryUpdateDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.util.exception.ConditionMismatchException;
import ru.practicum.ewm.util.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Создание категории;
     *
     * @param dto
     * @return CategoryDto
     */
    @Override
    @Transactional
    public CategoryDto create(CategoryCreateDto dto) {
        var category = categoryMapper.map(dto);
        category = categoryRepository.save(category);

        return categoryMapper.map(category);
    }

    /**
     * Обновление категории;
     *
     * @param dto
     * @param id
     * @return CategoryDto
     */
    @Override
    @Transactional
    public CategoryDto update(CategoryUpdateDto dto, long id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));

        categoryMapper.update(dto, category);
        category = categoryRepository.save(category);

        return categoryMapper.map(category);
    }

    /**
     * Удаление категории;
     *
     * @param id
     */
    @Override
    @Transactional
    public void delete(long id) {
        var events = eventRepository.getEventsByCategory_Id(id);

        if (events.isEmpty()) {
            categoryRepository.deleteById(id);
        } else {
            throw new ConditionMismatchException("The category is not empty");
        }
    }
}
