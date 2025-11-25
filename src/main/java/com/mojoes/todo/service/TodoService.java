package com.mojoes.todo.service;

import com.mojoes.todo.dto.TodoRequest;
import com.mojoes.todo.dto.TodoResponse;
import com.mojoes.todo.entity.Priority;
import com.mojoes.todo.entity.Status;

import java.time.LocalDate;
import java.util.List;

public interface TodoService {

    TodoResponse createTodo(TodoRequest request);

    List<TodoResponse> getTodosByCurrentUser();

    TodoResponse getById(Long id);

    TodoResponse updateTodo(Long id, TodoRequest request);

    void deleteTodo(Long id);

    List<TodoResponse> getTodosByFilters(Priority priority, Status status, LocalDate dueDate);
}
