package com.ylli.users_service.services.impls;

import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.dtos.UserDto;
import com.ylli.shared.models.User;
import com.ylli.users_service.mappers.UserMapper;
import com.ylli.users_service.repositories.UserRepository;
import com.ylli.users_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserDto, String, UserRepository, UserMapper> implements UserService {

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        super(userRepository, userMapper);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
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