package com.ylli.users_service.services.impls;

import com.ylli.shared.base.AuditHelper;
import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.configs.JwtUtil;
import com.ylli.shared.dtos.*;
import com.ylli.shared.enums.AuditType;
import com.ylli.users_service.configs.UserSpecification;
import com.ylli.users_service.dtos.UserLoginDto;
import com.ylli.shared.enums.UserRole;
import com.ylli.shared.models.User;
import com.ylli.users_service.mappers.UserMapper;
import com.ylli.users_service.repositories.UserRepository;
import com.ylli.users_service.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserDto, String, UserRepository, UserMapper> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditHelper auditHelper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuditHelper auditHelper) {
        super(userRepository, userMapper);
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.auditHelper = auditHelper;
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
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setBirthDate(dto.getBirthDate());
        user.setActive(true);
        user.setRoles(Set.of(UserRole.ROLE_USER));

        var savedUser = repository.save(user);
        var token = jwtUtil.generateToken(savedUser.getId(), savedUser.getRoles());
        log.info("User signed up successfully: {}", savedUser.getUsername());

        auditHelper.createAuditWithUser(
                AuditType.USER_SIGNED_UP,
                "User with username " + savedUser.getUsername() + " has signed up successfully.",
                savedUser.getId()
        );

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
            System.out.println(passwordEncoder.encode("Baba123@"));
            throw new IllegalArgumentException("Invalid username or password");
        }

        String accessToken = jwtUtil.generateToken(user.getId(), user.getRoles());
//        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        UserDto userDto = mapper.toDto(user);

        auditHelper.createAuditWithUser(
                AuditType.USER_LOGGED_IN,
                "User with username " + user.getUsername() + " has logged in successfully.",
                user.getId()
        );

        return new LoginResponseDto(accessToken, userDto);
    }

    @Override
    public UserDto getDefaultUser() {
        return repository.findByUsername("admin")
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public Page<UserDto> filterAdminUsers(String adminId, UserFilterDto filterDto, int page, int size) {
        validateAdmin(adminId);

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page number must be >= 0 and size must be > 0");
        }

        if (filterDto == null) {
            filterDto = new UserFilterDto();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> usersPage = repository.findAll(UserSpecification.filterUsers(filterDto), pageable);

        return usersPage.map(mapper::toDto);
    }

    @Override
    public Page<UserDto> getAllUsers(String adminId, int page, int size){
        validateAdmin(adminId);

        Pageable pageable  = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> users = repository.findAll(pageable);

        return users.map(mapper::toDto);
    }

    private void validateAdmin(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " does not have any roles");
        }

        if (!user.getRoles().contains(UserRole.ROLE_ADMIN)) {
            throw new IllegalArgumentException("User with ID " + userId + " is not an admin");
        }
    }
}