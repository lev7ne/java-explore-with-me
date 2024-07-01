package ru.ewm.category.service;

import org.springframework.data.domain.Pageable;
import ru.ewm.category.dto.CategoryDto;

import java.util.List;


public interface CategoryPublicService {
    List<CategoryDto> index(Pageable pageable);

    CategoryDto show(long id);
}
