package com.ecommerce.user.Service.impl;

import com.ecommerce.user.entity.User;
import com.ecommerce.user.exception.UserAlreadyExistsException;
import com.ecommerce.user.exception.UserNotFoundException;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static List<User> getUsers() {
        List<User> users=new ArrayList<>();
        User user=new User();
        user.setFirstName("John");
        user.setMiddleName("Doe");
        user.setLastName("Smith");
        user.setUsername("johndoe");
        user.setEmail("6Ft5P@example.com");
        user.setAddressId("123 Main St");
        users.add(user);
        User user1=new User();
        user1.setFirstName("Arun");
        user1.setMiddleName("Reddy");
        user1.setLastName("Marella");
        user1.setUsername("ArunReddy");
        user1.setEmail("arunreddy@gmail.com");
        user1.setAddressId("JNTU");
        users.add(user1);
        return users;
    }

    private static User getUser() {
        User user=new User();
        user.setFirstName("John");
        user.setMiddleName("Doe");
        user.setLastName("Smith");
        user.setUsername("johndoe");
        user.setEmail("6Ft5P@example.com");
        user.setAddressId("123 Main St");
        return user;
    }

    private static Page<User> getUserPage(Pageable pageable) {
        List<User> users=getUsers();
        Page<User> userPage=new PageImpl<>(users,pageable,users.size());
        return userPage;
    }

    @Test
    public void testGetUsers() {
        Pageable pageable = mock(Pageable.class);
        Page<User> expectedUsers = getUserPage(pageable);
        when(userRepository.findAll(pageable)).thenReturn(expectedUsers);
        Page<User> actualUsers = userService.getUsers(pageable);
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetUserById_ExistingUser() {
        String userId = "1";
        User expectedUser = getUser();
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(expectedUser));
        User actualUser = userService.getUserById(userId);
        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserById_NonExistingUser() {
        String userId = "1";
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testUserByUsername_ExistingUser() {
        String username = "johndoe";
        User expectedUser = getUser();
        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.of(expectedUser));
        User actualUser = userService.getUserByUsername(username);
        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testUserByUsername_NonExistingUser() {
        String username = "johndoe";
        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testCreateUser_Success() {
        User user = getUser();
        user.setUserId(null);
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User createdUser = userService.createUser(user);
        assertNotNull(createdUser.getUserId());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }
    @Test
    void testCreateNewUser_Success() {
        User user = getUser();
        user.setUserId(null);
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User createdUser = userService.createUser(user);
        assertNotNull(createdUser.getUserId());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }
    @Test
    void testUpdateUser_Success() {
        User user = getUser();
        when(userRepository.existsById(user.getUserId())).thenReturn(true);
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User updatedUser = userService.updateUser(user);
        assertNotNull(updatedUser);
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void testUpdateUser_Failure() {
        User user = getUser();
        when(userRepository.existsById(user.getUserId())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
        verify(userRepository, times(0)).saveAndFlush(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        String userId = "1";
        when(userRepository.existsById(userId)).thenReturn(true);
        userService.deleteUser(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_Failure() {
        String userId = "1";
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, times(0)).deleteById(userId);
    }

    /*@Test
    void testPatchUser_Success() {
        String userId = "1";
        User user = getUser();
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "John");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User patchedUser = userService.patchUser(userId, updates);
        assertNotNull(patchedUser);
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }*/
}
