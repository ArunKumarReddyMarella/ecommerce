package com.ecommerce.user.Controller;

import com.ecommerce.user.controller.UserController;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUsersDesc() {
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        String sortField = "username";
        Sort sort = Sort.by(Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> expectedUsers = getUsers(pageable);
        when(userService.getUsers(pageable)).thenReturn(expectedUsers);
        ResponseEntity<Page<User>> response = userController.getUsers(page, size, sortDirection, sortField);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
        verify(userService, times(1)).getUsers(pageable);
    }

    @Test
    void testGetUsersAsc() {
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        String sortField = "username";
        Sort sort = Sort.by(Sort.Direction.ASC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> expectedUsers = getUsers(pageable);
        when(userService.getUsers(pageable)).thenReturn(expectedUsers);
        ResponseEntity<Page<User>> response = userController.getUsers(page, size, sortDirection, sortField);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
        verify(userService, times(1)).getUsers(any(Pageable.class));
    }

    private static Page<User> getUsers(Pageable pageable) {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setFirstName("John");
        user.setMiddleName("Doe");
        user.setLastName("Smith");
        user.setUsername("johndoe");
        user.setEmail("6Ft5P@example.com");
        user.setAddressId("123 Main St");
        users.add(user);
        User user1 = new User();
        user1.setFirstName("Arun");
        user1.setMiddleName("Reddy");
        user1.setLastName("Marella");
        user1.setUsername("ArunReddy");
        user1.setEmail("arunreddy@gmail.com");
        user1.setAddressId("JNTU");
        users.add(user1);
        return new PageImpl<>(users, pageable, users.size());
    }

    private static User getUser() {
        User user = new User();
        user.setFirstName("John");
        user.setMiddleName("Doe");
        user.setLastName("Smith");
        user.setUsername("johndoe");
        user.setEmail("6Ft5P@example.com");
        user.setAddressId("123 Main St");
        return user;
    }

    @Test
    void testCreateUser() {
        User user = getUser();
        when(userService.createUser(any(User.class))).thenReturn(user);
        ResponseEntity<User> response = userController.createUser(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testGetUserById() {
        String userId = "1";
        User user = getUser();
        when(userService.getUserById(userId)).thenReturn(user);
        ResponseEntity<User> response = userController.getUserById(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testGetUserByUsername() {
        String username = "johndoe";
        User user = getUser();
        when(userService.getUserByUsername(username)).thenReturn(user);
        ResponseEntity<User> response = userController.getUserByUsername(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).getUserByUsername(username);
    }

    @Test
    void testUpdateUser() {
        String userId = "1";
        User user = getUser();
        when(userService.updateUser(any(User.class))).thenReturn(user);
        ResponseEntity<User> response = userController.updateUser(userId, user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    void testDeleteUser() {
        String userId = "1";
        ResponseEntity<String> response = userController.deleteUser(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully!", response.getBody());
        verify(userService, times(1)).deleteUser(userId);
    }

 /*   @Test
    void testPatchUser() {
        String userId = "1";
        Map<String, Object> updates = Map.of("firstName", "John", "lastName", "Doe");
        User user = getUser();
        when(userService.patchUser(userId, updates)).thenReturn(user);
        ResponseEntity<User> response = userController.patchUser(userId, updates);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).patchUser(userId, updates);
    }*/
}
