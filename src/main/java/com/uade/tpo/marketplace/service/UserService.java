package com.uade.tpo.marketplace.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.UserEditRequest;
import com.uade.tpo.marketplace.entity.dto.UserResponse;
import com.uade.tpo.marketplace.exceptions.UserDuplicateException;

public interface UserService {
    public UserResponse getUserById(Long userId, User requester);
    public Optional<UserResponse> getUserByEmail(String email, User requester);
    public Optional<UserResponse> getUserByUsername(String username, User requester);
    public User createUser(String email, String name, String surname, String username, String password, MultipartFile fileImage) throws UserDuplicateException, IOException;
    public User updateUser(User user, MultipartFile fileImage);
    public Boolean deleteUser(Long id);
    public UserResponse editUser(User user, UserEditRequest userRequest, MultipartFile fileImage) throws Exception;
}
