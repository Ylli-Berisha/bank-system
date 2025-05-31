package com.ylli.shared.dtos;

import com.ylli.shared.base.IdentifiableDto;
import com.ylli.shared.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements IdentifiableDto<String> {

    @Schema(
            description = "Unique identifier for the user (Pass as null or blank, id is generated server-side)",
            example = ""
    )
    private String id;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    @Schema(
            description = "Unique username of the user",
            example = "johndoe123"
    )
    private String username;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "Email address of the user", example = "johndoe@example.com")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    @Schema(description = "Phone number of the user", example = "+1234567890")
    private String phoneNumber;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Schema(description = "Address of the user", example = "123 Main St, Springfield, USA")
    private String address;

    @Past(message = "Birth date must be in the past")
    @Schema(description = "Birth date of the user", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "Indicates whether the user account is active", example = "true")
    private boolean isActive;

    @Schema(description = "List of roles assigned to the user", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private Set<UserRole> roles = new HashSet<>();

    @Schema(
            description = "User password (only for registration or internal use)",
            example = "SecurePassw0rd!"
    )

    @PastOrPresent(message = "Updated date must be in the past or present")
    private LocalDateTime updatedAt;

    @Schema(
            description = "Date and time when the user was created, value is generated server-side",
            example = ""
    )
    private LocalDateTime createdAt;
}
