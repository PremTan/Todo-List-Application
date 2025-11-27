package com.mojoes.todo.service.impl;

import com.mojoes.todo.dto.TodoRequest;
import com.mojoes.todo.dto.TodoResponse;
import com.mojoes.todo.entity.Priority;
import com.mojoes.todo.entity.Status;
import com.mojoes.todo.entity.Todo;
import com.mojoes.todo.entity.User;
import com.mojoes.todo.exception.ResourceNotFoundException;
import com.mojoes.todo.mapper.TodoMapper;
import com.mojoes.todo.repository.TodoRepository;
import com.mojoes.todo.repository.UserRepository;
import com.mojoes.todo.security.SecurityUtil;
import com.mojoes.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public TodoResponse createTodo(TodoRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        log.info("Creating todo for user : {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Todo todo = TodoMapper.toEntity(request);
        todo.setUser(user);

        log.debug("Saving todo : {}", todo);
        Todo saved = todoRepository.save(todo);

        return TodoMapper.toDto(saved);
    }

    @Override
    public Page<TodoResponse> getTodosByCurrentUser(int page, int size, String sortBy, String sortDir,
                                                    String text, Priority priority, Status status, LocalDate dueDate) {
        String email = SecurityUtil.getCurrentUserEmail();
        log.info("Fetching todos of user : {} by pagination and filtering",email);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Todo> todos;
        if (text != null && !text.isEmpty()) {
            todos = todoRepository.findByUserEmailAndTitleContainingIgnoreCase(email, text, pageRequest);
        } else {
            todos = todoRepository.findByUserEmail(email, pageRequest);
        }

        Stream<Todo> todos2 = todos.stream();

        if (priority != null) {
            todos2 = todos2.filter(t -> t.getPriority() == priority);
        }
        if (status != null) {
            todos2 = todos2.filter(t -> t.getStatus() == status);
        }
        if (dueDate != null) {
            todos2 = todos2.filter(t -> t.getDueDate().equals(dueDate));
        }

        List<Todo> filteredList = todos2.toList();

        log.info("Filtered todos count : {}", filteredList.size());

        List<TodoResponse> responseList = filteredList.stream()
                .map(TodoMapper::toDto)
                .toList();

        return new PageImpl<>(responseList, todos.getPageable(), todos.getTotalElements());
    }

    @Override
    public TodoResponse getById(Long id) {
        String email = SecurityUtil.getCurrentUserEmail();
        log.info("Fetching todo by todo id = {} of user = {}", id, email);

        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getEmail().equals(email))
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found"));
        return TodoMapper.toDto(todo);
    }

    @Transactional
    @Override
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        log.info("Updating todo.. id = {} of user = {}", id, email);

        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getEmail().equals(email))
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found"));

        TodoMapper.updateEntity(todo, request);

        log.debug("Saving update todo : {}", todo);
        Todo saved = todoRepository.save(todo);

        return TodoMapper.toDto(saved);
    }

    @Transactional
    @Override
    public void deleteTodo(Long id) {
        String email = SecurityUtil.getCurrentUserEmail();
        log.info("deleting todo, id = {} of user = {}", id, email);

        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getEmail().equals(email))
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found"));
        todoRepository.delete(todo);
        log.info("Todo deleted successfully : id = {}", id);
    }

    @Transactional
    @Override
    public void importTodosFromFile(MultipartFile file) {
        String email = SecurityUtil.getCurrentUserEmail();
        log.info("Importing todos from CSV file for user = {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            reader.readLine();

            List<Todo> todoList = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] arrayData = line.split(",");
                Todo todo = new Todo();
                todo.setTitle(arrayData[0]);
                todo.setDescription(arrayData[1]);
                todo.setPriority(Priority.valueOf(arrayData[2]));
                todo.setStatus(Status.valueOf(arrayData[3]));
                todo.setDueDate(LocalDate.parse(arrayData[4]));
                todo.setUser(user);

                todoList.add(todo);
            }
            todoRepository.saveAll(todoList);
            log.info("File import successfully for user = {}", email);
        } catch (Exception e) {
            log.error("Error.. importing todos from file : {}", e.getMessage());
        }
    }

}
