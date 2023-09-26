package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.util.exception.ConditionMismatchException;
import ru.practicum.ewm.util.helper.ObjectFinder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    /**
     * Endpoint: POST "/admin/categories"
     *
     * @param newCategoryDto
     * @return CategoryDto
     */
    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        return CategoryMapper.toCategoryDtoFromCategory(
                categoryRepository.save(CategoryMapper.toCategoryFromCategoryDto(newCategoryDto))
        );
    }

    /**
     * Endpoint: PATCH "/admin/categories/{catId}"
     *
     * @param newCategoryDto
     * @param catId
     * @return CategoryDto
     */
    @Override
    @Transactional
    public CategoryDto update(NewCategoryDto newCategoryDto, Long catId) {
        Category updatedCategory = ObjectFinder.findCategoryById(categoryRepository, catId);

        updatedCategory.setName(newCategoryDto.getName());

        return CategoryMapper.toCategoryDtoFromCategory(categoryRepository.save(updatedCategory));
    }

    /**
     * Endpoint: DELETE "/admin/categories/{catId}"
     *
     * @param catId
     */
    @Override
    @Transactional
    public void delete(Long catId) {
        ObjectFinder.findCategoryById(categoryRepository, catId);

        List<Event> eventsByCategory = eventRepository.getEventsByCategory_Id(catId);

        if (!eventsByCategory.isEmpty()) {
            throw new ConditionMismatchException("The category is not empty");
        }

        categoryRepository.deleteById(catId);
    }
}
