package com.ylli.users_service.services.impls;

import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.configs.JwtUtil;
import com.ylli.shared.dtos.LoginResponseDto;
import com.ylli.shared.dtos.SignUpResponseDto;
import com.ylli.shared.dtos.UserDto;
import com.ylli.users_service.dtos.UserLoginDto;
import com.ylli.shared.enums.UserRole;
import com.ylli.shared.models.User;
import com.ylli.users_service.mappers.UserMapper;
import com.ylli.users_service.repositories.UserRepository;
import com.ylli.users_service.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.ylli.shared.dtos.UserSignUpDto;


@Slf4j
@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserDto, String, UserRepository, UserMapper> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        super(userRepository, userMapper);
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SignUpResponseDto signUp(UserSignUpDto dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setBirthDate(dto.getBirthDate());
        user.setActive(true);
        user.setRoles(Set.of(UserRole.ROLE_USER));

        var savedUser = repository.save(user);
        var token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRoles());
        log.info("User signed up successfully: {}", savedUser.getUsername());

        return new SignUpResponseDto(
                mapper.toDto(user),
                token
        );
    }

    @Override
    public LoginResponseDto login(UserLoginDto loginDto) {
        log.info("login user: {}", loginDto);
        User user = repository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!user.isActive()) {
            throw new IllegalStateException("User account is deactivated");
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            log.info("users password: {}", user.getPassword());
            log.info("loginDto password: {}", loginDto.getPassword());
//            throw new IllegalArgumentException("Invalid username or password");
        }

        String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRoles());
//        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        UserDto userDto = mapper.toDto(user);

        return new LoginResponseDto(accessToken, userDto);
    }

//    @Override
//    public UserDto validateUser(String username) {
//        User user = repository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        return mapper.toDto(user);
//    }


}






//    @Service
//    @RequiredArgsConstructor
//    public class UserServiceImpl implements UserService {
//
//        private final UserRepository userRepository;
//
//        private final UserMapper userMapper;
//
//        @Autowired
//        public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
//            this.userRepository = userRepository;
//            this.userMapper = userMapper;
//        }
//
//        @Override
//        public UserDto getById(String id) {
//            User user = userRepository.findById(id)
//                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
//            return mapToDto(user);
//        }
//
//        @Override
//        public UserDto create(UserDto userDto) {
//            User user = mapToEntity(userDto);
//            user.setId(null);
//            user.setCreatedAt(LocalDateTime.now());
//            user.setUpdatedAt(LocalDateTime.now());
//            User savedUser = userRepository.save(user);
//            return mapToDto(savedUser);
//        }
//
//        @Override
//        public UserDto update(String id, UserDto userDto) {
//            User existingUser = userRepository.findById(id)
//                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
//
//            User updatedUser = mapToEntity(userDto);
//            updatedUser.setId(id);
//            updatedUser.setCreatedAt(existingUser.getCreatedAt());
//            updatedUser.setUpdatedAt(LocalDateTime.now());
//
//            User savedUser = userRepository.save(updatedUser);
//            return mapToDto(savedUser);
//        }
//
//        @Override
//        public UserDto delete(String id) {
//            User user = userRepository.findById(id)
//                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
//            userRepository.deleteById(id);
//            return mapToDto(user);
//        }
//
//        @Override
//        public List<UserDto> getAllUsers() {
//            return userRepository.findAll().stream()
//                    .map(this::mapToDto)
//                    .collect(Collectors.toList());
//        }
//
//        private UserDto mapToDto(User user) {
//            return userMapper.toDto(user);
//        }
//
//        private User mapToEntity(UserDto userDto) {
//            return userMapper.toEntity(userDto);
//        }
//    }