package com.uade.tpo.marketplace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.UserResponse;
import com.uade.tpo.marketplace.exceptions.UserDuplicateException;
import com.uade.tpo.marketplace.repository.ProductRepository;
import com.uade.tpo.marketplace.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Optional<UserResponse> getUserById(Long userId, User requester) {
        boolean isAdmin = requester.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return userRepository.findById(userId)
            .map(user -> {
                List<Product> products = productRepository.findByOwner(userId);
                if (requester.getId().equals(userId) || isAdmin) {
                    return UserResponse.full(user, products);
                } else {
                    return UserResponse.limited(user, products);
                }
            });
    }

    @Override
    public Optional<UserResponse> getUserByEmail(String email, User requester) {
        boolean isAdmin = requester.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return userRepository.findByEmail(email)
            .map(user -> {
                List<Product> products = productRepository.findByOwner(user.getId());
                if (requester.getId().equals(user.getId()) || isAdmin) {
                    return UserResponse.full(user, products);
                } else {
                    return UserResponse.limited(user, products);
                }
            });
    }

    @Override
    public Optional<UserResponse> getUserByUsername(String username, User requester) {
        boolean isAdmin = requester.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));



        return userRepository.findByUsername(username)
            .map(user -> {
                List<Product> products = productRepository.findByOwner(user.getId());
                if (requester.getId().equals(user.getId()) || isAdmin) {
                    return UserResponse.full(user, products);
                } else {
                    return UserResponse.limited(user, products);
                }
            });
    }

    @Override
    public User createUser(String email, String name, String surname, String username, String password) throws UserDuplicateException {
        // verify if email exists in bd
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserDuplicateException("El email ya está registrado: " + email);
        }

        // create user with default value
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setPassword(password);
        if (email.toLowerCase().trim().endsWith("@colecxion.com")) {
            user.setRole(User.RoleName.ADMIN); // if colecxion.com, admin
        } else {
            user.setRole(User.RoleName.USER);  // default value
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        // Verify user in BD
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // update variables
        existingUser.setUsername(user.getUsername());
        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole()); // change of role


        // save changes
        return userRepository.save(existingUser);
    }

    @Override
    public Boolean deleteUser(Long id) { //delete user by id
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }
}