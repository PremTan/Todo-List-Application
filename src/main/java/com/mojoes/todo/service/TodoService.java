package com.mojoes.todo.service;

import com.mojoes.todo.dto.TodoRequest;
import com.mojoes.todo.dto.TodoResponse;
import com.mojoes.todo.entity.Priority;
import com.mojoes.todo.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface TodoService {

    TodoResponse createTodo(TodoRequest request);

    Page<TodoResponse> getTodosByCurrentUser(int page, int size, String sortBy, String sortDir, String text);

    TodoResponse getById(Long id);

    TodoResponse updateTodo(Long id, TodoRequest request);

    void deleteTodo(Long id);

    List<TodoResponse> getTodosByFilters(Priority priority, Status status, LocalDate dueDate);

    void importTodosFromFile(MultipartFile file);
}
