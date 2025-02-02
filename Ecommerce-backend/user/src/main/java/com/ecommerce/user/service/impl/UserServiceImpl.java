package com.ecommerce.user.service.impl;

import com.ecommerce.order.service.OrderService;
import com.ecommerce.user.dto.OrderedProduct;
import com.ecommerce.user.entity.User;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.user.exception.UserAlreadyExistsException;
import com.ecommerce.user.exception.UserNotFoundException;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final OrderService orderService;

    public UserServiceImpl(UserRepository userRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserById(String userId) {
        User optionalUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return optionalUser;
    }

    @Override
    public User getUserByUsername(String username) {
        User optionalUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return optionalUser;
    }

    @Override
    public User createUser(User user) {
        if(user.getUserId() == null){
            user.setUserId(UUID.randomUUID().toString());
        }
        else{
            Optional<User> existingUser = userRepository.findById(user.getUserId());
            if (existingUser.isPresent()) {
                throw new UserAlreadyExistsException("User with ID " + user.getUserId() + " already exists.");
            }
        }
        return userRepository.saveAndFlush(user);
    }

    @Override
    public void deleteUser(String userId) {
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public User updateUser(User updatedUser) {
        if (!userRepository.existsById(updatedUser.getUserId())) {
            throw new UserNotFoundException("User not found with ID: " + updatedUser.getUserId());
        }
        return userRepository.saveAndFlush(updatedUser);
    }

    @Override
    public void patchUser(String userId, Map<String, Object> updates) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingUser, updates);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }

        userRepository.save(existingUser);
    }

}
