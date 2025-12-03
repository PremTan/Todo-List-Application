package com.mojoes.todo.dto;

import com.mojoes.todo.entity.Priority;
import com.mojoes.todo.entity.Status;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TodoRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Status is required")
    private Status status;

    @FutureOrPresent(message = "Due date must not be in past")
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
}
