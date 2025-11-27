package com.mojoes.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "Password required")
    @Size(min = 6, message = "Password must be >= 6 characters")
    private String oldPassword;

    @NotBlank(message = "Password required")
    @Size(min = 6, message = "Password must be >= 6 characters")
    private String newPassword;
}
