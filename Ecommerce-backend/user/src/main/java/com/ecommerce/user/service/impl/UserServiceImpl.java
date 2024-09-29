package com.ecommerce.user.service.impl;

import com.ecommerce.order.service.OrderService;
import com.ecommerce.user.dto.OrderedProduct;
import com.ecommerce.user.entity.User;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.service.UserService;
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
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return null;
        }
        return optionalUser.get();
    }

    @Override
    public User createUser(User user) {
        if(user.getUserId() == null){
            user.setUserId(UUID.randomUUID().toString());
        }
        else{
            Optional<User> existingUser = userRepository.findById(user.getUserId());
            if (existingUser.isPresent()) {
                throw new RuntimeException("User with ID " + user.getUserId() + " already exists.");
            }
        }
        return userRepository.saveAndFlush(user);
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User updateUser(User updatedUser) {
        return userRepository.saveAndFlush(updatedUser);
    }

    @Override
    public void patchUser(String userId, Map<String, Object> updates) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        updates.forEach((key, value) -> {
            try {
                // Use reflection or property accessors to update specific fields based on the key
                Field field = User.class.getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType() == Timestamp.class) {
                    try {
                        // Use OffsetDateTime to parse the ISO 8601 format
                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
                        field.set(existingUser, timestampValue);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid format for "+key+" TimeStamp field");
                    }
                } else {
                    // Set other field types as usual
                    field.set(existingUser, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Handle potential exceptions (e.g., invalid field name)
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });


        userRepository.save(existingUser);
    }

    @Override
    public Page<OrderedProduct> getOrderedProducts(String userId, Pageable pageable) {
        var orderedItems = orderService.getOrderItemsByUserId(userId, pageable);
        List<OrderedProduct> orderedProducts = orderedItems.getContent().stream().map(orderItem -> {
           var OrderedProduct = new OrderedProduct();
           OrderedProduct.setProductId(orderItem.getProductId());
           OrderedProduct.setQuantity(orderItem.getQuantity());
           OrderedProduct.setTotalPrice(orderItem.getPrice());
           return OrderedProduct;
        }).collect(Collectors.toList());

        return new PageImpl<>(orderedProducts, pageable, orderedItems.getTotalElements());
    }
}
