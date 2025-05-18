package com.ylli.users_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.UserDto;
import com.ylli.users_service.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Users",
        description = "Operations related to users"
)
@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController<UserDto, String, UserService> {

    @Autowired
    public UserController(UserService userService) {
        super(userService);
    }
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