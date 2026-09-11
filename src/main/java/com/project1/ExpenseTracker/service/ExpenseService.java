package com.project1.ExpenseTracker.service;

import com.project1.ExpenseTracker.dto.ExpenseRequest;
import com.project1.ExpenseTracker.dto.ExpenseResponse;
import com.project1.ExpenseTracker.entity.Expense;
import com.project1.ExpenseTracker.entity.User;
import com.project1.ExpenseTracker.exception.ResourceNotFoundException;
import com.project1.ExpenseTracker.repository.ExpenseRepository;
import com.project1.ExpenseTracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;


@Service
public class ExpenseService {
    private static final Logger logger =
            LoggerFactory.getLogger(ExpenseService.class);

    private final ExpenseRepository expenseRepository;

    private final UserRepository userRepository;

    private final FileStorageService fileStorageService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository, FileStorageService fileStorageService) {

        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    // -----------------------------
    // Helper Method 1
    // Get Current Logged-in User
    // -----------------------------
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
    }

    // -----------------------------
    // Helper Method 2
    // Convert Entity -> DTO
    // -----------------------------
    private ExpenseResponse mapToResponse(
            Expense expense) {

        ExpenseResponse response =
                new ExpenseResponse();

        response.setId(expense.getId());

        response.setTitle(expense.getTitle());

        response.setAmount(expense.getAmount());

        response.setCategory(expense.getCategory());

        response.setDate(expense.getDate());

        response.setDescription(
                expense.getDescription());
        response.setReceiptPath(
                expense.getReceiptPath());

        return response;
    }
    // -------------------------------------
// Add Expense
// -------------------------------------
    public ExpenseResponse addExpense(
            ExpenseRequest request) {

        User user = getCurrentUser();

        logger.info("Creating expense for user: {}",
                user.getUsername());

        Expense expense = new Expense();

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setUser(user);

        logger.debug("Expense Title: {}",
                request.getTitle());

        Expense savedExpense = expenseRepository.save(expense);

        logger.info("Expense created successfully with id: {}",
                savedExpense.getId());

        return mapToResponse(savedExpense);
    }
    // -------------------------------------
// Get All Expenses of Current User
// -------------------------------------
    public List<ExpenseResponse> getAllExpenses() {

        User user = getCurrentUser();

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        return expenses.stream()

                .map(this::mapToResponse)

                .toList();
    }
    // -------------------------------------
// Get Expense By Id
// -------------------------------------
    public ExpenseResponse getExpenseById(Long id) {

        User user = getCurrentUser();

        Expense expense =
                expenseRepository

                        .findByIdAndUser(id, user)

                        .orElseThrow(() ->

                                new ResourceNotFoundException(
                                        "Expense not found with id : " + id));

        return mapToResponse(expense);
    }
    // -------------------------------------
// Update Expense
// -------------------------------------
    public ExpenseResponse updateExpense(
            Long id,
            ExpenseRequest request) {

        logger.info("Updating expense id {}", id);

        User user = getCurrentUser();

        Expense expense = expenseRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense not found with id : " + id));

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());

        Expense updatedExpense =
                expenseRepository.save(expense);

        logger.info("Expense {} updated successfully", id);

        return mapToResponse(updatedExpense);
    }
    // -------------------------------------
// Delete Expense
// -------------------------------------
    public void deleteExpense(Long id) {

        logger.info("Deleting expense id {}", id);

        User user = getCurrentUser();

        Expense expense = expenseRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense not found with id : " + id));

        expenseRepository.delete(expense);

        logger.info("Expense {} deleted successfully", id);
    }
    // -------------------------------------
// Search Expense By Title
// -------------------------------------
    public List<ExpenseResponse> searchExpenses(
            String title) {
        logger.info("Searching expenses with title {}",
                title);
        User user = getCurrentUser();

        List<Expense> expenses =
                expenseRepository
                        .findByTitleContainingIgnoreCaseAndUser(
                                title,
                                user);

        return expenses.stream()

                .map(this::mapToResponse)

                .toList();
    }
    // -------------------------------------
// Filter Expenses
// -------------------------------------
    public List<ExpenseResponse> getExpensesByCategory(
            String category) {

        logger.info("Filtering expenses by category {}",
                category);

        User user = getCurrentUser();

        List<Expense> expenses =
                expenseRepository
                        .findByCategoryAndUser(
                                category,
                                user);

        return expenses.stream()
                .map(this::mapToResponse)
                .toList();
    }
    // -------------------------------------
// Pagination
// -------------------------------------
    public Page<ExpenseResponse> getExpenses(
            int page,
            int size) {

        logger.info("Fetching page {} with size {}", page, size);

        User user = getCurrentUser();

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Expense> expensePage =
                expenseRepository.findByUser(user, pageable);

        return expensePage.map(this::mapToResponse);
    }

    public ExpenseResponse uploadReceipt(
            Long expenseId,
            MultipartFile file) {

        User user = getCurrentUser();

        logger.info(
                "Uploading receipt for expense {} by user {}",
                expenseId,
                user.getUsername());

        Expense expense = expenseRepository
                .findByIdAndUser(expenseId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense not found with id : "
                                        + expenseId));

        String filePath =
                fileStorageService.storeFile(file);

        expense.setReceiptPath(filePath);

        Expense savedExpense =
                expenseRepository.save(expense);

        logger.info(
                "Receipt uploaded successfully for expense {}",
                expenseId);

        return mapToResponse(savedExpense);
    }
    public ResponseEntity<Resource> downloadReceipt(
            Long expenseId) {

        // -------------------------------------
        // 1. Get current logged-in user
        // -------------------------------------

        User user = getCurrentUser();

        logger.info(
                "Downloading receipt for expense {} by user {}",
                expenseId,
                user.getUsername());

        // -------------------------------------
        // 2. Find expense belonging to user
        // -------------------------------------

        Expense expense =
                expenseRepository
                        .findByIdAndUser(
                                expenseId,
                                user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Expense not found with id : "
                                                + expenseId));

        // -------------------------------------
        // 3. Check receipt exists
        // -------------------------------------

        if (expense.getReceiptPath() == null ||
                expense.getReceiptPath().isBlank()) {

            throw new ResourceNotFoundException(
                    "No receipt found for expense : "
                            + expenseId);
        }

        // -------------------------------------
        // 4. Load file
        // -------------------------------------

        Path filePath =
                fileStorageService.loadFile(
                        expense.getReceiptPath());

        // -------------------------------------
        // 5. Create Resource
        // -------------------------------------

        Resource resource =
                new FileSystemResource(filePath);

        // -------------------------------------
        // 6. Determine content type
        // -------------------------------------

        String contentType;

        try {

            contentType =
                    Files.probeContentType(filePath);

        } catch (Exception e) {

            contentType = null;
        }

        if (contentType == null) {

            contentType =
                    "application/octet-stream";
        }

        // -------------------------------------
        // 7. Log successful download
        // -------------------------------------

        logger.info(
                "Receipt downloaded successfully for expense {}",
                expenseId);

        // -------------------------------------
        // 8. Return file
        // -------------------------------------

        return ResponseEntity.ok()

                .contentType(
                        MediaType.parseMediaType(
                                contentType))

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                filePath.getFileName()
                                        .toString() +
                                "\"")

                .body(resource);
    }

}
