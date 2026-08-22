package com.nexuscore.webportal.service;

import com.nexuscore.webportal.model.AppUser;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface mapping all valid business logic operations for the User entity.
 */
public interface UserService {
    
    /**
     * Saves a user to the repository (either creating new or updating existing).
     * @param user The user object to persist.
     * @return The persisted user object.
     */
    AppUser saveUser(AppUser user);

    /**
     * Creates a new user and starts the welcome-email process.
     * @param user The new user to persist.
     * @return The persisted user object.
     */
    AppUser registerUser(AppUser user);

    /**
     * Retrieves a user by their primary key.
     * @param id The ID to search for.
     * @return An Optional containing the user if found.
     */
    Optional<AppUser> findById(int id);

    /**
     * Retrieves a user by their unique username.
     * @param username The exact string username.
     * @return An Optional containing the user if found.
     */
    Optional<AppUser> findByUsername(String username);

    /**
     * Retrieves a list of all users in the system.
     * @return A list of AppUser objects.
     */
    List<AppUser> findAllUsers();

    /**
     * Deletes a user from the repository based on ID.
     * @param id The ID of the user to delete.
     */
    void deleteUser(int id);

    /**
     * Validates a raw password against the stored BCrypt hash for a username.
     * @param username The username attempting authentication.
     * @param password The raw string password.
     * @return true if valid, false otherwise.
     */
    boolean authenticate(String username, String password);
}
