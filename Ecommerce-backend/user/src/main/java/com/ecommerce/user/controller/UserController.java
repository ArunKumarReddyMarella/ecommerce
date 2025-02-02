package com.ecommerce.user.controller;

import com.ecommerce.user.dto.OrderedProduct;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "username"); // Sort by username by default
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users = userService.getUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username")
    public ResponseEntity<User> getUserByUsername(@RequestParam("username") String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User user) {
        user.setUserId(userId); // Ensure ID matches path variable
        User updatedUser = userService.updateUser(user);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> patchUser(@PathVariable String userId, @RequestBody Map<String, Object> updates) {
        User existingUser = userService.getUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        userService.patchUser(userId, updates);
        User updatedUser = userService.getUserById(userId);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully!");
    }
}
