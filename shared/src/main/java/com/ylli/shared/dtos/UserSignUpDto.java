package com.ylli.shared.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data required for user registration")
public class UserSignUpDto {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    @Schema(description = "First name of the user", example = "Ylli")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    @Schema(description = "Last name of the user", example = "Berisha")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email
    @Size(max = 100)
    @Schema(description = "Email address of the user", example = "ylli@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Password must include at least one digit, one lowercase, one uppercase, and one special character"
    )
    @Schema(description = "Password for the user account. Must include upper/lowercase, digit, and special character", example = "MyStrongP@ssword1")
    private String password;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    @Schema(description = "User's phone number", example = "+38349123456")
    private String phoneNumber;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Schema(description = "User's address", example = "Rr. Garibaldi, Prishtinë")
    private String address;

    @Past(message = "Birth date must be in the past")
    @Schema(description = "Date of birth of the user", example = "2000-04-15")
    private LocalDate birthDate;
}
