package com.mojoes.todo.controller;

import com.mojoes.todo.dto.TodoRequest;
import com.mojoes.todo.dto.TodoResponse;
import com.mojoes.todo.entity.Priority;
import com.mojoes.todo.entity.Status;
import com.mojoes.todo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService service;

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTodo(req));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TodoResponse>> getByUserId(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(service.getTodoByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable("id") Long id, @Valid @RequestBody TodoRequest req){
        return ResponseEntity.ok(service.updateTodo(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id){
        service.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TodoResponse>> getByFilters(@RequestParam Long userId,
                                                           @RequestParam(required = false) Priority priority,
                                                           @RequestParam(required = false) Status status,
                                                           @RequestParam(required = false) LocalDate dueDate){
        return ResponseEntity.ok(service.getTodosByFilters(userId, priority, status, dueDate));
    }
}
