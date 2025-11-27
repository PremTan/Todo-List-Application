package com.mojoes.todo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @Email(message = "Invalid email")
    @NotBlank(message = "Email required")
    private String email;

    @Size(min = 6, max = 6, message = "OTP must be exact 6 digits")
    private String otp;

    @NotBlank(message = "Password required")
    @Size(min = 6, message = "Password must be >= 6 characters")
    private String newPassword;
}
