package com.mojoes.todo.dto;

import com.mojoes.todo.entity.Priority;
import com.mojoes.todo.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoResponse {

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDate dueDate;
    private Long userId;
}
