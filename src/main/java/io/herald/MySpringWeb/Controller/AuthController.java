package io.herald.MySpringWeb.Controller;

import io.herald.MySpringWeb.Model.UserTable;
import io.herald.MySpringWeb.Service.UserService;
import io.herald.MySpringWeb.Configuration.JWUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST API Controller for authentication (login/signup).
 * Returns JSON responses instead of HTML views.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWUtil jwUtil;

    /**
     * Handles user login via REST API.
     * Returns JWT token on successful authentication.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        Map<String, Object> response = new HashMap<>();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            response.put("message", "Username and password are required");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.authenticate(username, password)) {
            String token = jwUtil.generateToken(username);
            response.put("token", token);
            response.put("username", username);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Invalid username or password");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Handles user signup via REST API.
     * Creates a new user account and returns success/error message.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        
        Map<String, Object> response = new HashMap<>();

        if (username == null || username.isBlank() || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            response.put("message", "Please complete all fields");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.findByUsername(username).isPresent()) {
            response.put("message", "Username already in use");
            return ResponseEntity.status(409).body(response);
        }

        try {
            UserTable newUser = new UserTable();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);
            
            userService.saveUser(newUser);
            response.put("message", "User registered successfully");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
