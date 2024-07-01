package ru.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.category.dto.CategoryCreateDto;
import ru.ewm.category.dto.CategoryDto;
import ru.ewm.category.dto.CategoryUpdateDto;
import ru.ewm.category.mapper.CategoryMapper;
import ru.ewm.category.repository.CategoryRepository;
import ru.ewm.event.model.Event;
import ru.ewm.event.repository.EventRepository;
import ru.ewm.util.exception.ConditionMismatchException;
import ru.ewm.util.exception.NotFoundException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Создание и добавление новой категории в репозиторий (БД).
     *
     * @param createDto - DTO для создания новой категории
     * @return CategoryDto - DTO, возвращаемый пользователю
     */
    @Override
    @Transactional
    public CategoryDto create(CategoryCreateDto createDto) {
        var category = categoryMapper.toEntity(createDto);
        category = categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    /**
     * Обновление информации о категории в репозитории (БД).
     *
     * @param updateDto - DTO для обновления категории
     * @param id        - идентификатор обновляемой категории
     * @return UserDto - DTO, возвращаемый пользователю
     * @throws NotFoundException если не удалось найти категорию по полученному идентификатору
     */
    @Override
    @Transactional
    public CategoryDto update(CategoryUpdateDto updateDto, long id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));

        categoryMapper.update(updateDto, category);
        category = categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    /**
     * Удаление категории из репозитория (БД).
     *
     * @param id - идентификатор пользователя
     * @throws ConditionMismatchException если есть ивенты (Event), относящиеся к этой категории
     */
    @Override
    @Transactional
    public void delete(long id) {
        List<Event> events = eventRepository.findAllByCategoryId(id);

        if (!events.isEmpty()) {
            throw new ConditionMismatchException("The category is not empty");
        }

        categoryRepository.deleteById(id);
    }
}
