package com.ylli.shared.dtos;

import com.ylli.shared.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDto {
    private UserDto userObj;
    private String accessToken;
}
