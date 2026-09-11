package com.project1.ExpenseTracker.repository;


import com.project1.ExpenseTracker.entity.Expense;
import com.project1.ExpenseTracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);

    Optional<Expense> findByIdAndUser(Long id, User user);

    List<Expense> findByTitleContainingIgnoreCaseAndUser(
            String title,
            User user
    );

    List<Expense> findByCategoryAndUser(
            String category,
            User user
    );

    Page<Expense> findByUser(
            User user,
            Pageable pageable
    );
}