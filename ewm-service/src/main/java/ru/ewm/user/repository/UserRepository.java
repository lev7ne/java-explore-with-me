package ru.ewm.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ewm.user.model.User;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    List<User> getUsersByIdIn(List<Long> ids, Pageable pageable);
}
