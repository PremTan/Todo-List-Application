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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setId(1L);
        user.setEmail("prem@gmail.com");
    }

    @Test
    void createTodo() {
        TodoRequest request = new TodoRequest("Task1", "Do this task..", Priority.HIGH, Status.PENDING, LocalDate.now());
        Todo todo = TodoMapper.toEntity(request);
        todo.setUser(user);

        MockedStatic<SecurityUtil> sec = mockStatic(SecurityUtil.class);
        sec.when(SecurityUtil::getCurrentUserEmail).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoResponse todo1 = todoService.createTodo(request);

        assertNotNull(todo1);
        assertEquals("Task1", todo1.getTitle());

        sec.close();
    }

    @Test
    void getById() {
        Todo todo = Todo.builder().id(1L).title("Task1").user(user).build();

        MockedStatic<SecurityUtil> sec = mockStatic(SecurityUtil.class);
        sec.when(SecurityUtil::getCurrentUserEmail).thenReturn(user.getEmail());
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        TodoResponse resp = todoService.getById(1L);

        assertEquals("Task1", resp.getTitle());

        sec.close();
    }

    @Test
    void deleteTodo() {
        Todo todo = Todo.builder().id(1L).title("Delete").user(user).build();

        MockedStatic<SecurityUtil> sec = mockStatic(SecurityUtil.class);
        sec.when(SecurityUtil::getCurrentUserEmail).thenReturn(user.getEmail());
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        todoService.deleteTodo(1L);

        sec.close();
    }
}