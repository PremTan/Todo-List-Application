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
import com.mojoes.todo.security.SecurityUtil;
import com.mojoes.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public TodoResponse createTodo(TodoRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setPriority(request.getPriority());
        todo.setStatus(request.getStatus());
        todo.setDueDate(request.getDueDate());
        todo.setUser(user);
        Todo saved = todoRepository.save(todo);

        return mapper.map(saved, TodoResponse.class);
    }

    @Override
    public Page<TodoResponse> getTodosByCurrentUser(int page, int size, String sortBy, String sortDir, String text) {
        String email = SecurityUtil.getCurrentUserEmail();

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Todo> todos;
        if(text != null && !text.isEmpty()){
            todos = todoRepository.findByUserEmailAndTitleContainingIgnoreCase(email, text, pageRequest);
        } else{
            todos = todoRepository.findByUserEmail(email, pageRequest);
        }

        return todos.map(todo -> mapper.map(todo, TodoResponse.class));
    }

    @Override
    public TodoResponse getById(Long id) {
        String email = SecurityUtil.getCurrentUserEmail();
        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getEmail().equals(email))
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found"));
        return mapper.map(todo, TodoResponse.class);
    }

    @Override
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getEmail().equals(email))
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found"));

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDueDate(request.getDueDate());
        todo.setPriority(request.getPriority());
        todo.setStatus(request.getStatus());

        Todo saved = todoRepository.save(todo);

        return mapper.map(saved, TodoResponse.class);
    }

    @Override
    public void deleteTodo(Long id) {
        String email = SecurityUtil.getCurrentUserEmail();
        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getEmail().equals(email))
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found"));
        todoRepository.delete(todo);
    }

    @Override
    public List<TodoResponse> getTodosByFilters(Priority priority, Status status, LocalDate dueDate) {

        String email = SecurityUtil.getCurrentUserEmail();
        List<Todo> todos = todoRepository.findByUserEmail(email);

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
