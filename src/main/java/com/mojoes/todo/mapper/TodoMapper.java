package com.mojoes.todo.mapper;

import com.mojoes.todo.dto.TodoRequest;
import com.mojoes.todo.dto.TodoResponse;
import com.mojoes.todo.entity.Todo;

public class TodoMapper {

    public static Todo toEntity(TodoRequest request){
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setPriority(request.getPriority());
        todo.setStatus(request.getStatus());
        todo.setDueDate(request.getDueDate());
        return todo;
    }

    public static TodoResponse toDto(Todo todo) {
        TodoResponse dto = new TodoResponse();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setPriority(todo.getPriority());
        dto.setStatus(todo.getStatus());
        dto.setDueDate(todo.getDueDate());
        return dto;
    }

    public static void updateEntity(Todo todo, TodoRequest request){
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setPriority(request.getPriority());
        todo.setStatus(request.getStatus());
        todo.setDueDate(request.getDueDate());
    }
}
