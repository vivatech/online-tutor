package com.vivatech.onlinetutor.webchat.service;

import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.webchat.dto.CreateUserRequest;
import com.vivatech.onlinetutor.webchat.dto.UserDto;
import com.vivatech.onlinetutor.webchat.model.User;
import com.vivatech.onlinetutor.webchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserDto createUser(CreateUserRequest request) {
        // Check if username or email already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new OnlineTutorExceptionHandler("Username already exists");
        }
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new OnlineTutorExceptionHandler("Email already exists");
        }

        if (userRepository.existsByMsisdn(request.getMsisdn())) {
            throw new OnlineTutorExceptionHandler("Msisdn already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setMsisdn(request.getMsisdn());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setProfilePicture(request.getProfilePicture());
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(this::convertToDto);
    }
    
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::convertToDto);
    }
    
    public List<UserDto> getAllActiveUsers() {
        return userRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<UserDto> getUsersByRole(User.UserRole role) {
        return userRepository.findActiveUsersByRole(role)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<UserDto> searchUsers(String searchTerm) {
        return userRepository.searchActiveUsers(searchTerm)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public UserDto updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if username or email conflicts with other users
        Optional<User> existingByUsername = userRepository.findByUsername(request.getUsername());
        if (existingByUsername.isPresent() && !existingByUsername.get().getId().equals(id)) {
            throw new RuntimeException("Username already exists");
        }
        
        Optional<User> existingByEmail = userRepository.findByEmail(request.getEmail());
        if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(id)) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setUsername(request.getUsername());
        user.setMsisdn(request.getMsisdn());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setProfilePicture(request.getProfilePicture());
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setMsisdn(user.getMsisdn());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}