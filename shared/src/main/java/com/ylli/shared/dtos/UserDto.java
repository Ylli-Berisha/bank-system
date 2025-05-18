package com.ylli.shared.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Schema(
            description = "Unique identifier for the user (Pass as null or blank, id is generated server-side)",
            example = ""
    )
    private String id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(
            description = "First name of the user",
            example = "John"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(
            description = "Last name of the user",
            example = "Doe"
    )
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(
            description = "Email address of the user",
            example = "johndoe@example.com"
    )
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    @Schema(
            description = "Phone number of the user",
            example = "+1234567890"
    )
    private String phoneNumber;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Schema(
            description = "Address of the user",
            example = "123 Main St, Springfield, USA"
    )
    private String address;

    @Past(message = "Birth date must be in the past")
    @Schema(
            description = "Birth date of the user",
            example = "1990-01-01"
    )
    private LocalDate birthDate;

    @PastOrPresent(message = "Created date must be in the past or present")
    private LocalDateTime updatedAt;

    @Schema(
            description = "Date and time when the user was created, value is generated server-side, pass as null or empty",
            example = ""
    )
    private LocalDateTime createdAt;

}
