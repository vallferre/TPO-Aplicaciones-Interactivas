package com.uade.tpo.marketplace.service;

import java.util.Optional;

import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.UserResponse;

public interface UserService {
    public Optional<UserResponse> getUserById(Long userId, User requester);
    public Optional<UserResponse> getUserByEmail(String email, User requester);
    public Optional<UserResponse> getUserByUsername(String username, User requester);
    public User createUser(String email, String name, String surname, String username, String password);
    public User updateUser(User user);
    public Boolean deleteUser(Long id);
}
