//package com.example.eventifyeventmanagment;
//
//import com.example.eventifyeventmanagment.Exceptions.DuplicateEmailException;
//import com.example.eventifyeventmanagment.dto.request.UserRegistrationDTO;
//import com.example.eventifyeventmanagment.entity.User;
//import com.example.eventifyeventmanagment.service.UserService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//
//public class UserServiceTest {
//
//    @Test
//    void shouldRegisterUserSuccessfully() throws DuplicateEmailException {
//        // Arrange
//        UserRegistrationDTO registrationDto = new UserRegistrationDTO("John", "john@example.com", "password123");
//        UserService userService = new UserService();
//        // Act
//        User savedUser = userService.registerUser(registrationDto);
//
//        // Assert
//        Assertions.assertNotNull(savedUser);
//        Assertions.assertEquals("John", savedUser.getName());
//        Assertions.assertEquals("john@example.com", savedUser.getEmail());
//        Assertions.assertNotNull(savedUser.getRegisteredOn());
//
//        // Verify user is saved in the database
////        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
////        Assertions.assertTrue(retrievedUser.isPresent());
////        Assertions.assertEquals("john@example.com", retrievedUser.get().getEmail());
//    }
//
//    @Test
//    void shouldThrowExceptionWhenEmailAlreadyExists() {
//        UserService userservice = new UserService();
//        // Arrange
//        User existingUser = new User();
//        existingUser.setName("John");
//        existingUser.setEmail("john@example.com");
//        existingUser.setPassword("password123");
//        existingUser.setRegisteredOn(LocalDateTime.now());
//        //  userRepository.save(existingUser);
//
//        UserRegistrationDTO registrationDto = new UserRegistrationDTO("Jane", "john@example.com", "password456");
//
//
//        // Act & Assert
//        DuplicateEmailException exception = Assertions.assertThrows(
//                DuplicateEmailException.class,
//                () -> userservice.registerUser(registrationDto)
//        );
//
//        Assertions.assertEquals("Email already preserntjohn@example.com", exception.getMessage());
//    }
//
//    @Test
//    void shouldSetRegisteredOnField() throws DuplicateEmailException {
//        // Arrange
//        UserRegistrationDTO registrationDto = new UserRegistrationDTO("Jane", "jane@example.com", "password123");
//        UserService userservice = new UserService();
//        // Act
//        User savedUser = userservice.registerUser(registrationDto);
//
//        // Assert
//        Assertions.assertNotNull(savedUser.getRegisteredOn());
//        Assertions.assertTrue(savedUser.getRegisteredOn().isBefore(LocalDateTime.now().plusSeconds(1)));
//
//        // Verify user is saved in the database
//        // Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
//        //  Assertions.assertTrue(retrievedUser.isPresent());
//        //  Assertions.assertNotNull(retrievedUser.get().getRegisteredOn());
//    }
//}
