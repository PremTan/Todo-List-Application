package com.mojoes.todo.repository;

import com.mojoes.todo.dto.TodoResponse;
import com.mojoes.todo.entity.Priority;
import com.mojoes.todo.entity.Status;
import com.mojoes.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUserEmail(String email);

    List<Todo> findByUserEmailAndPriority(String email, Priority priority);

    List<Todo> findByUserEmailAndStatus(String email, Status status);

    List<Todo> findByUserEmailAndDueDate(String email, LocalDate dueDate);

    Page<Todo> findByUserEmail(String email, Pageable pageable);

    Page<Todo> findByUserEmailAndTitleContainingIgnoreCase(String email, String  text, Pageable pageable);
}
