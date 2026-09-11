package com.project1.ExpenseTracker;



import com.project1.ExpenseTracker.dto.ExpenseRequest;
import com.project1.ExpenseTracker.dto.ExpenseResponse;
import com.project1.ExpenseTracker.entity.Expense;
import com.project1.ExpenseTracker.entity.User;
import com.project1.ExpenseTracker.exception.ResourceNotFoundException;
import com.project1.ExpenseTracker.repository.ExpenseRepository;
import com.project1.ExpenseTracker.repository.UserRepository;

import com.project1.ExpenseTracker.service.ExpenseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ExpenseService expenseService;


    @BeforeEach
    void setUp() {

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }


    @AfterEach
    void tearDown() {

        SecurityContextHolder.clearContext();
    }
    private User createUser(String username) {

        User user = new User();

        user.setId(1L);
        user.setUsername(username);
        user.setEmail(username + "@gmail.com");
        user.setPassword("encodedPassword");

        return user;
    }
    private User mockLoggedInUser(String username) {

        User user = createUser(username);

        when(authentication.getName())
                .thenReturn(username);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        return user;
    }
    @Test
    void addExpense_shouldCreateExpenseSuccessfully() {

        // ARRANGE

        mockLoggedInUser("akshita");

        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("Laptop");
        request.setAmount(50000.0);
        request.setCategory("Electronics");
        request.setDate(LocalDate.of(2026, 8, 15));
        request.setDescription("New laptop");


        Expense savedExpense = new Expense();

        savedExpense.setId(1L);
        savedExpense.setTitle("Laptop");
        savedExpense.setAmount(50000.0);
        savedExpense.setCategory("Electronics");
        savedExpense.setDate(
                LocalDate.of(2026, 8, 15));
        savedExpense.setDescription("New laptop");


        when(expenseRepository.save(any(Expense.class)))
                .thenReturn(savedExpense);


        // ACT

        ExpenseResponse response =
                expenseService.addExpense(request);


        // ASSERT

        assertNotNull(response);

        assertEquals(1L, response.getId());

        assertEquals(
                "Laptop",
                response.getTitle());

        assertEquals(
                50000.0,
                response.getAmount());

        assertEquals(
                "Electronics",
                response.getCategory());


        verify(expenseRepository)
                .save(any(Expense.class));
    }
    @Test
    void getExpenseById_shouldReturnExpense_whenExpenseExists() {

        // ARRANGE

        User user = mockLoggedInUser("akshita");

        Expense expense = new Expense();

        expense.setId(10L);
        expense.setTitle("Food");
        expense.setAmount(500.0);
        expense.setCategory("Food");
        expense.setDate(
                LocalDate.of(2026, 8, 15));
        expense.setDescription("Lunch");
        expense.setUser(user);


        when(expenseRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(expense));


        // ACT

        ExpenseResponse response =
                expenseService.getExpenseById(10L);


        // ASSERT

        assertNotNull(response);

        assertEquals(
                10L,
                response.getId());

        assertEquals(
                "Food",
                response.getTitle());

        assertEquals(
                500.0,
                response.getAmount());


        verify(expenseRepository)
                .findByIdAndUser(10L, user);
    }
    @Test
    void getExpenseById_shouldThrowException_whenExpenseDoesNotExist() {

        // ARRANGE

        User user = mockLoggedInUser("akshita");


        when(expenseRepository.findByIdAndUser(
                999L,
                user))
                .thenReturn(Optional.empty());


        // ACT + ASSERT

        assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.getExpenseById(999L)
        );


        verify(expenseRepository)
                .findByIdAndUser(999L, user);
    }
    @Test
    void updateExpense_shouldUpdateExistingExpense() {

        // ARRANGE

        User user = mockLoggedInUser("akshita");


        Expense existingExpense = new Expense();

        existingExpense.setId(10L);
        existingExpense.setTitle("Old Title");
        existingExpense.setAmount(100.0);
        existingExpense.setCategory("Food");
        existingExpense.setDate(
                LocalDate.of(2026, 8, 10));
        existingExpense.setDescription("Old");
        existingExpense.setUser(user);


        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("New Title");
        request.setAmount(200.0);
        request.setCategory("Shopping");
        request.setDate(
                LocalDate.of(2026, 8, 15));
        request.setDescription("Updated");


        when(expenseRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(existingExpense));


        when(expenseRepository.save(existingExpense))
                .thenReturn(existingExpense);


        // ACT

        ExpenseResponse response =
                expenseService.updateExpense(
                        10L,
                        request);


        // ASSERT

        assertEquals(
                "New Title",
                response.getTitle());

        assertEquals(
                200.0,
                response.getAmount());

        assertEquals(
                "Shopping",
                response.getCategory());

        assertEquals(
                "Updated",
                response.getDescription());


        verify(expenseRepository)
                .save(existingExpense);
    }
    @Test
    void updateExpense_shouldThrowException_whenExpenseDoesNotExist() {

        // ARRANGE

        User user = mockLoggedInUser("akshita");


        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("New Title");
        request.setAmount(200.0);
        request.setCategory("Food");
        request.setDate(
                LocalDate.of(2026, 8, 15));
        request.setDescription("Updated");


        when(expenseRepository.findByIdAndUser(
                999L,
                user))
                .thenReturn(Optional.empty());


        // ACT + ASSERT

        assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.updateExpense(
                        999L,
                        request)
        );


        verify(
                expenseRepository,
                never())
                .save(any(Expense.class));
    }
    @Test
    void deleteExpense_shouldDeleteExistingExpense() {

        // ARRANGE

        User user = mockLoggedInUser("akshita");


        Expense expense = new Expense();

        expense.setId(10L);
        expense.setTitle("Food");
        expense.setAmount(500.0);
        expense.setUser(user);


        when(expenseRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(expense));


        // ACT

        expenseService.deleteExpense(10L);


        // ASSERT

        verify(expenseRepository)
                .delete(expense);
    }
    @Test
    void deleteExpense_shouldThrowException_whenExpenseDoesNotExist() {

        // ARRANGE

        User user = mockLoggedInUser("akshita");


        when(expenseRepository.findByIdAndUser(
                999L,
                user))
                .thenReturn(Optional.empty());


        // ACT + ASSERT

        assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.deleteExpense(999L)
        );


        verify(
                expenseRepository,
                never())
                .delete(any(Expense.class));
    }
    @Test
    void deleteExpense_shouldReject_whenExpenseBelongsToAnotherUser() {

        // ARRANGE

        User rahul = mockLoggedInUser("rahul");


        /*
         * Expense #10 belongs to Akshita.
         *
         * Rahul is currently logged in.
         *
         * Therefore:
         *
         * findByIdAndUser(10, rahul)
         *
         * should return nothing.
         */

        when(expenseRepository.findByIdAndUser(
                10L,
                rahul))
                .thenReturn(Optional.empty());


        // ACT + ASSERT

        assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.deleteExpense(10L)
        );


        // Rahul must NOT be able to delete
        // Akshita's expense.

        verify(
                expenseRepository,
                never())
                .delete(any(Expense.class));
    }

}
