package com.project1.ExpenseTracker.controller;

import com.project1.ExpenseTracker.dto.ExpenseRequest;
import com.project1.ExpenseTracker.dto.ExpenseResponse;
import com.project1.ExpenseTracker.service.ExpenseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Tag(
        name = "Expense Management",
        description = "APIs for managing user expenses"
)
@RestController
@RequestMapping("/expenses")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;


    public ExpenseController(
            ExpenseService expenseService) {

        this.expenseService = expenseService;
    }


    // =====================================================
    // ADD EXPENSE
    // =====================================================

    @Operation(
            summary = "Add Expense",
            description = "Creates a new expense for the currently logged-in user."
    )
    @PostMapping
    public ExpenseResponse addExpense(
            @Valid @RequestBody ExpenseRequest request) {

        return expenseService.addExpense(request);
    }


    // =====================================================
    // GET ALL EXPENSES
    // =====================================================

    @Operation(
            summary = "Get All Expenses",
            description = "Returns all expenses belonging to the currently logged-in user."
    )
    @GetMapping
    public List<ExpenseResponse> getAllExpenses() {

        return expenseService.getAllExpenses();
    }


    // =====================================================
    // GET EXPENSE BY ID
    // =====================================================

    @Operation(
            summary = "Get Expense By ID",
            description = "Returns a single expense using its ID. Only the owner can access it."
    )
    @GetMapping("/{id}")
    public ExpenseResponse getExpenseById(

            @Parameter(
                    description = "ID of the expense",
                    example = "1"
            )
            @PathVariable Long id) {

        return expenseService.getExpenseById(id);
    }


    // =====================================================
    // UPDATE EXPENSE
    // =====================================================

    @Operation(
            summary = "Update Expense",
            description = "Updates an existing expense belonging to the currently logged-in user."
    )
    @PutMapping("/{id}")
    public ExpenseResponse updateExpense(

            @Parameter(
                    description = "ID of the expense to update",
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody ExpenseRequest request) {

        return expenseService.updateExpense(
                id,
                request
        );
    }


    // =====================================================
    // DELETE EXPENSE
    // =====================================================

    @Operation(
            summary = "Delete Expense",
            description = "Deletes an expense belonging to the currently logged-in user."
    )
    @DeleteMapping("/{id}")
    public String deleteExpense(

            @Parameter(
                    description = "ID of the expense to delete",
                    example = "1"
            )
            @PathVariable Long id) {

        expenseService.deleteExpense(id);

        return "Expense deleted successfully";
    }


    // =====================================================
    // SEARCH EXPENSES
    // =====================================================

    @Operation(
            summary = "Search Expenses",
            description = "Searches the current user's expenses by title."
    )
    @GetMapping("/search")
    public List<ExpenseResponse> searchExpenses(

            @Parameter(
                    description = "Title or part of the title to search for",
                    example = "Food"
            )
            @RequestParam String title) {

        return expenseService.searchExpenses(title);
    }


    // =====================================================
    // FILTER BY CATEGORY
    // =====================================================

    @Operation(
            summary = "Filter Expenses By Category",
            description = "Returns expenses belonging to a specific category."
    )
    @GetMapping("/category")
    public List<ExpenseResponse> getExpensesByCategory(

            @Parameter(
                    description = "Expense category",
                    example = "Food"
            )
            @RequestParam String category) {

        return expenseService.getExpensesByCategory(category);
    }


    // =====================================================
    // PAGINATION
    // =====================================================

    @Operation(
            summary = "Get Paginated Expenses",
            description = "Returns the current user's expenses page by page."
    )
    @GetMapping("/page")
    public Page<ExpenseResponse> getExpenses(

            @Parameter(
                    description = "Page number starting from 0",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    description = "Number of expenses per page",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            int size) {

        return expenseService.getExpenses(
                page,
                size
        );
    }


    // =====================================================
    // UPLOAD RECEIPT
    // =====================================================

    @Operation(
            summary = "Upload Expense Receipt",
            description = "Uploads a PDF, JPG or PNG receipt for an expense. Only the expense owner can upload a receipt."
    )
    @PostMapping("/{id}/receipt")
    public ExpenseResponse uploadReceipt(

            @Parameter(
                    description = "ID of the expense",
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    description = "Receipt file. Allowed formats: PDF, JPG, JPEG and PNG. Maximum size: 10 MB."
            )
            @RequestPart("file") MultipartFile file) {

        return expenseService.uploadReceipt(
                id,
                file
        );
    }


    // =====================================================
    // DOWNLOAD RECEIPT
    // =====================================================

    @Operation(
            summary = "Download Expense Receipt",
            description = "Downloads the receipt attached to an expense. Only the expense owner can download the receipt."
    )
    @GetMapping("/{id}/receipt")
    public ResponseEntity<Resource> downloadReceipt(

            @Parameter(
                    description = "ID of the expense",
                    example = "1"
            )
            @PathVariable Long id) {

        return expenseService.downloadReceipt(id);
    }
}