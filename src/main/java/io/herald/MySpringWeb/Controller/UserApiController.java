package io.herald.MySpringWeb.Controller;

import io.herald.MySpringWeb.Model.UserTable;
import io.herald.MySpringWeb.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST API Controller for user management.
 * Provides endpoints for CRUD operations on users.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserApiController {

    @Autowired
    private UserService userService;

    /**
     * Get all users.
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserTable> users = userService.findAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching users");
        }
    }

    /**
     * Get a specific user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        try {
            Optional<UserTable> user = userService.findById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            }
            return ResponseEntity.status(404).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching user");
        }
    }

    /**
     * Create a new user.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        
        Map<String, Object> response = new HashMap<>();

        if (username == null || username.isBlank() || email == null || email.isBlank()) {
            response.put("message", "Username and email are required");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.findByUsername(username).isPresent()) {
            response.put("message", "Username already exists");
            return ResponseEntity.status(409).body(response);
        }

        try {
            UserTable newUser = new UserTable();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword("defaultPass123"); // Default password for API-created users
            
            userService.saveUser(newUser);
            response.put("message", "User created successfully");
            response.put("user", newUser);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            response.put("message", "Error creating user: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Update an existing user.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        
        Map<String, Object> response = new HashMap<>();

        if (username == null || username.isBlank() || email == null || email.isBlank()) {
            response.put("message", "Username and email are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Optional<UserTable> existingUser = userService.findById(id);
            if (existingUser.isEmpty()) {
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }

            UserTable user = existingUser.get();
            user.setUsername(username);
            user.setEmail(email);
            
            userService.saveUser(user);
            response.put("message", "User updated successfully");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error updating user: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Delete a user.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<UserTable> existingUser = userService.findById(id);
            if (existingUser.isEmpty()) {
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }

            userService.deleteUser(id);
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error deleting user: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
