package com.ylli.shared.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username of the user", example = "ylliberisha")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password of the user", example = "MySecureP@ssword1!")
    private String password;
}
