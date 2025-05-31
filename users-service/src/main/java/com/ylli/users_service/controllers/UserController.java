package com.ylli.users_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.configs.JwtUtil;
import com.ylli.shared.dtos.LoginResponseDto;
import com.ylli.shared.dtos.UserDto;
import com.ylli.shared.dtos.UserLoginDto;
import com.ylli.shared.dtos.UserSignUpDto;
import com.ylli.users_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Users",
        description = "Operations related to users"
)
@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController<UserDto, String, UserService> {

    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        super(userService);
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided signup information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already in use"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody UserSignUpDto dto) {
        service.signUp(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Operation(
            summary = "User login",
            description = "Authenticates a user with the provided credentials."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody UserLoginDto dto
    ) {
        LoginResponseDto user = service.login(dto);
        return ResponseEntity.ok(user);
    }

//    @PostMapping("/refresh")
//    @Operation(summary = "Refresh JWT tokens", description = "Get new access and refresh tokens using refresh token")
//    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody String refreshToken) {
//        if (!jwtUtil.validateToken(refreshToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        String username = jwtUtil.getUsernameFromToken(refreshToken);
//        String newAccessToken = jwtUtil.generateToken(username);
//        String newRefreshToken = jwtUtil.generateRefreshToken(username);
//
//        UserDto userDto = service.validateUser(username);
//
//        LoginResponseDto response = new LoginResponseDto(newAccessToken, newRefreshToken, userDto);
//
//        return ResponseEntity.ok(response);
//    }
}



//@RestController
//@RequestMapping("/api/users/")
//@RequiredArgsConstructor
//@Validated
//public class UserController {
//
//    private final UserService userService;
//
//    @Autowired
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<UserDto>> getAllUsers() {
//        return ResponseEntity.ok(userService.getAllUsers());
//    }
//
//    @GetMapping("{id}")
//    public ResponseEntity<UserDto> getUserById(@PathVariable @NotBlank(message = "ID cannot be blank") String id) {
//        return ResponseEntity.ok(userService.getById(id));
//    }
//
//    @PostMapping("create")
//    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
//        return new ResponseEntity<>(userService.create(userDto), HttpStatus.CREATED);
//    }
//
//    @PutMapping("update/{id}")
//    public ResponseEntity<UserDto> updateUser(
//            @PathVariable @NotBlank(message = "ID cannot be blank") String id,
//            @Valid @RequestBody UserDto userDto) {
//        return ResponseEntity.ok(userService.update(id, userDto));
//    }
//
//    @DeleteMapping("delete/{id}")
//    public ResponseEntity<UserDto> deleteUser(@PathVariable @NotBlank(message = "ID cannot be blank") String id) {
//        return ResponseEntity.ok(userService.delete(id));
//    }
//}