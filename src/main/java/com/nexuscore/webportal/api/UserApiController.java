package com.nexuscore.webportal.api;

import com.nexuscore.webportal.exception.UserNotFoundException;
import com.nexuscore.webportal.model.AppUser;
import com.nexuscore.webportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Core REST Controller providing CRUD operations for the User entity.
 * All endpoints are prefixed with /api and require JWT authentication.
 */
@RestController
@RequestMapping("/api")
public class UserApiController {

    @Autowired
    private UserService userService;

    /**
     * Simple health-check endpoint.
     * @return Hello World string.
     */
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World");
    }

    /**
     * Retrieves all registered users.
     * @return List of users or HTTP 204 NO CONTENT if empty.
     */
    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        List<AppUser> users = userService.findAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a specific user by their ID.
     * @param id The ID of the target user.
     * @return User details.
     * @throws UserNotFoundException if the user does not exist.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable int id) {
        Optional<AppUser> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            // Exception will be caught by the GlobalExceptionHandler
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }

    /**
     * Creates a new user via API.
     * @param user The AppUser payload.
     * @return Success message with HTTP 201 CREATED status.
     */
    @PostMapping("/users")
    public ResponseEntity<String> saveUser(@RequestBody AppUser user) {
        userService.registerUser(user);
        return new ResponseEntity<>("Saved Successfully", HttpStatus.CREATED);
    }

    /**
     * Updates an existing user's details.
     * @param id The ID of the user to update.
     * @param userDetails The new user details payload.
     * @return The updated AppUser object.
     * @throws UserNotFoundException if the user doesn't exist.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<AppUser> updateUser(@PathVariable int id, @RequestBody AppUser userDetails) {
        Optional<AppUser> optionalUser = userService.findById(id);
        
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            // Update mutable fields
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            
            // Only update password if a new one is provided in the request
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(userDetails.getPassword());
            }
            
            AppUser updatedUser = userService.saveUser(user);
            return ResponseEntity.ok(updatedUser);
        } else {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }

    /**
     * Deletes a user by ID.
     * @param id The ID of the user to delete.
     * @return HTTP 204 NO CONTENT on success.
     * @throws UserNotFoundException if user doesn't exist.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        Optional<AppUser> user = userService.findById(id);
        
        if (user.isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }
}
