package com.nexuscore.webportal.service;

import com.nexuscore.webportal.model.AppUser;
import com.nexuscore.webportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Concrete implementation of the UserService interface.
 * Handles the core business logic involving User CRUD, password encryption, and post-creation events (emails).
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Saves a user while protecting a raw password before database persistence.
     * This method is also used by profile updates, so it does not send emails.
     */
    @Override
    public AppUser saveUser(AppUser user) {
        // Only hash password if it is not already a BCrypt hash.
        // This prevents double-hashing on update operations.
        if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    /**
     * Persists a new account, then sends its welcome email without delaying the HTTP response.
     */
    @Override
    public AppUser registerUser(AppUser user) {
        AppUser savedUser = saveUser(user);
        if (savedUser.getEmail() != null) {
            emailService.sendRegistrationEmail(savedUser.getEmail(), savedUser.getUsername());
        }
        return savedUser;
    }

    @Override
    public Optional<AppUser> findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<AppUser> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    /**
     * Resolves the user from the repository and uses BCrypt to verify the raw password match.
     */
    @Override
    public boolean authenticate(String username, String password) {
        Optional<AppUser> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            // Uses BCrypt match function to verify the hash securely
            return passwordEncoder.matches(password, user.get().getPassword());
        }
        return false;
    }
}
