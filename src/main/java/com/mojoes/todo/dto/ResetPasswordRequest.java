package com.mojoes.todo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @Email
    @NotBlank
    private String email;
    @Size(min = 6, max = 6)
    private String otp;
    @Size(min = 6)
    private String newPassword;
}
