package com.mojoes.todo.controller;

import com.mojoes.todo.dto.TodoRequest;
import com.mojoes.todo.dto.TodoResponse;
import com.mojoes.todo.entity.Priority;
import com.mojoes.todo.entity.Status;
import com.mojoes.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
@Tag(name = "Todo Controller", description = "User operations on Todos")
public class TodoController {

    private final TodoService service;

    @Operation(summary = "Create a new Todo",
            description = "Create a new task for logged in user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Todo created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data")})
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTodo(req));
    }

    @Operation(summary = "Get paginated Todos of current user",
            description = "Fetch Todos of logged-in user with pagination, sorting and filers like Priority, Status, DueDate and search")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todos fetch successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user")})
    @GetMapping
    public ResponseEntity<Page<TodoResponse>> getCurrentUserTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) LocalDate dueDate) {
        return ResponseEntity.ok(service.getTodosByCurrentUser(page, size, sortBy, sortDir, search, priority, status, dueDate));
    }

    @Operation(summary = "Get todo by ID",
            description = "Fetch single todo of current logged in user by todo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo fetch successfully"),
            @ApiResponse(responseCode = "404", description = "Todo not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")})
    @GetMapping("/todo/{id}")
    public ResponseEntity<TodoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Update Todo",
            description = "Update current logged in user Todo by todo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update data"),
            @ApiResponse(responseCode = "404", description = "Todo not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")})
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable("id") Long id, @Valid @RequestBody TodoRequest req) {
        return ResponseEntity.ok(service.updateTodo(id, req));
    }

    @Operation(summary = "Delete Todo",
            description = "Delete current logged in user Todo by todo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todo deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Todo not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        service.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Import Todos from file",
            description = "Import Todos for current logged in user by CSV file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todos imported successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @PostMapping("/import")
    public ResponseEntity<String> importTodos(@RequestParam("file") MultipartFile file) {
        service.importTodosFromFile(file);
        return ResponseEntity.ok("Todos imported successfully..");
    }
}
