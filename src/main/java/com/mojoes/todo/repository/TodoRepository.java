package com.mojoes.todo.repository;

import com.mojoes.todo.entity.Priority;
import com.mojoes.todo.entity.Status;
import com.mojoes.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUserId(Long id);

    List<Todo> findByUserIdAndPriority(Long userId, Priority priority);

    List<Todo> findByUserIdAndStatus(Long userId, Status status);

    List<Todo> findByUserIdAndDueDate(Long userId, LocalDate dueDate);
}
