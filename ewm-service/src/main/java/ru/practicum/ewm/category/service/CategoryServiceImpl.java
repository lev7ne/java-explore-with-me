package ru.practicum.ewm.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.util.helper.ObjectHelper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public NewCategoryDto save(NewCategoryDto newCategoryDto) {
        return CategoryMapper.toCategoryDtoFromCategory(categoryRepository.save(CategoryMapper.toCategoryFromCategoryDto(newCategoryDto)));
    }

    @Override
    @Transactional
    public NewCategoryDto update(NewCategoryDto newCategoryDto, Long catId) {
        Category updatedCategory = ObjectHelper.findCategoryById(categoryRepository, catId);
        updatedCategory.setName(newCategoryDto.getName());
        return CategoryMapper.toCategoryDtoFromCategory(categoryRepository.save(updatedCategory));
    }

    @Override
    @Transactional
    public void deleteById(Long catId) {
        ObjectHelper.findCategoryById(categoryRepository, catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewCategoryDto> findAll(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from, size)).stream()
                .map(CategoryMapper::toCategoryDtoFromCategory)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NewCategoryDto findById(Long catId) {
        return CategoryMapper.toCategoryDtoFromCategory(ObjectHelper.findCategoryById(categoryRepository, catId));
    }
}
