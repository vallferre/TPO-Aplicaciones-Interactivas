package com.uade.tpo.marketplace.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.CategoryImage;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.UserImage;
import com.uade.tpo.marketplace.entity.dto.UserEditRequest;
import com.uade.tpo.marketplace.entity.dto.UserResponse;
import com.uade.tpo.marketplace.exceptions.UserDuplicateException;
import com.uade.tpo.marketplace.exceptions.UserNotFoundException;
import com.uade.tpo.marketplace.repository.ProductRepository;
import com.uade.tpo.marketplace.repository.UserImageRepository;
import com.uade.tpo.marketplace.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserImageRepository imageRepository;

    @Override
    public UserResponse getUserById(Long userId, User requester) {
        boolean isAdmin = requester.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));

        List<Product> products = productRepository.findByOwner(userId);

        if (requester.getId().equals(userId) || !isAdmin) {
            return UserResponse.full(user, products);
        } else if (user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("No se puede acceder a la información de un usuario administrador.");
        } else {
            return UserResponse.limited(user, products);
        }
    }


    @Override
    public Optional<UserResponse> getUserByEmail(String email, User requester) {
        boolean isAdmin = requester.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return userRepository.findByEmail(email)
            .map(user -> {
                List<Product> products = productRepository.findByOwner(user.getId());
                if (requester.getId().equals(user.getId()) || !isAdmin) {
                    return UserResponse.full(user, products);
                } else if (user.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                    throw new RuntimeException("No se puede acceder a la información de un usuario administrador.");
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
                if (requester.getId().equals(user.getId()) || !isAdmin) {
                    return UserResponse.full(user, products);
                } else if (user.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                    throw new RuntimeException("No se puede acceder a la información de un usuario administrador.");
                } else {
                    return UserResponse.limited(user, products);
                }
            });
    }

    @Override
    public User createUser(String email, String name, String surname, String username, String password, MultipartFile fileImage) throws UserDuplicateException, IOException {
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

        UserImage userImage = null;

        if (fileImage != null && !fileImage.isEmpty()) {
            userImage = new UserImage();
            userImage.setContentType(fileImage.getContentType());
            userImage.setFilename(fileImage.getOriginalFilename());
            userImage.setData(fileImage.getBytes());
            userImage.setSize(fileImage.getSize());
            userImage = imageRepository.save(userImage); // guardo la imagen primero
        }
        
        user.setUserImagen(userImage);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user, MultipartFile fileImage) {
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


   @Override
    public UserResponse editUser(User user, UserEditRequest userRequest, MultipartFile fileImage) throws Exception {

        if (userRequest.getName() != null && !userRequest.getName().trim().isEmpty()) {
            user.setName(userRequest.getName().trim());
        }
        if (userRequest.getSurname() != null && !userRequest.getSurname().trim().isEmpty()) {
            user.setSurname(userRequest.getSurname().trim());
        }
        if (userRequest.getEmail() != null && !userRequest.getEmail().trim().isEmpty()) {
            user.setEmail(userRequest.getEmail().trim());
        }
        if (userRequest.getUsername() != null && !userRequest.getUsername().trim().isEmpty()) {
            user.setUsername(userRequest.getUsername().trim());
        }
        if (userRequest.getPassword() != null && !userRequest.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword().trim()));
        }

        if (fileImage != null && !fileImage.isEmpty()) {
            UserImage userImage = user.getUserImagen();
            if (userImage == null) {
                userImage = new UserImage();
            }

            userImage.setContentType(fileImage.getContentType());
            userImage.setFilename(fileImage.getOriginalFilename());
            userImage.setData(fileImage.getBytes());
            userImage.setSize(fileImage.getSize());

            // Guardamos la imagen
            userImage = imageRepository.save(userImage);

            // Asignamos la imagen actualizada al usuario
            user.setUserImagen(userImage);
        }

        return UserResponse.full(userRepository.save(user), productRepository.findByOwner(user.getId()));
    }



    
}