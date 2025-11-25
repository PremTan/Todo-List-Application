package com.mojoes.todo.service.impl;

import com.mojoes.todo.dto.TodoRequest;
import com.mojoes.todo.dto.TodoResponse;
import com.mojoes.todo.dto.UserResponse;
import com.mojoes.todo.entity.Priority;
import com.mojoes.todo.entity.Status;
import com.mojoes.todo.entity.Todo;
import com.mojoes.todo.entity.User;
import com.mojoes.todo.exception.ResourceNotFoundException;
import com.mojoes.todo.repository.TodoRepository;
import com.mojoes.todo.repository.UserRepository;
import com.mojoes.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public TodoResponse createTodo(TodoRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + request.getUserId()));

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setPriority(request.getPriority());
        todo.setStatus(request.getStatus());
        todo.setDueDate(request.getDueDate());
        todo.setUser(user);
        Todo saved = todoRepository.save(todo);

        TodoResponse map = mapper.map(saved, TodoResponse.class);
        map.setUserId(user.getId());
        return map;
    }

    @Override
    public List<TodoResponse> getTodoByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return todoRepository.findByUserId(userId)
                .stream()
                .map(todo -> mapper.map(todo, TodoResponse.class))
                .toList();
    }

    @Override
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ToDo list not found with id : " + id));
        
        if (!todo.getUser().getId().equals(request.getUserId())) {
            throw new ResourceNotFoundException("Owner can only update his todo");
        }

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDueDate(request.getDueDate());
        todo.setPriority(request.getPriority());
        todo.setStatus(request.getStatus());

        Todo saved = todoRepository.save(todo);

        TodoResponse map = mapper.map(saved, TodoResponse.class);
        map.setUserId(saved.getUser().getId());
        return map;
    }

    @Override
    public void deleteTodo(Long id) {
        if(!todoRepository.existsById(id)){
            throw new ResourceNotFoundException("ToDo list not found with id : " + id);
        }
        todoRepository.deleteById(id);
    }

    @Override
    public List<TodoResponse> getTodosByFilters(Long userId, Priority priority, Status status, LocalDate dueDate) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Todo> todos = todoRepository.findByUserId(userId);

        if (priority != null) {
            todos = todos.stream().filter(t -> t.getPriority() == priority).toList();
        }
        if (status != null) {
            todos = todos.stream().filter(t -> t.getStatus() == status).toList();
        }
        if (dueDate != null) {
            todos = todos.stream().filter(t -> t.getDueDate().equals(dueDate)).toList();
        }

        return todos.stream()
                .map(todo -> mapper.map(todo, TodoResponse.class))
                .toList();
    }

}
