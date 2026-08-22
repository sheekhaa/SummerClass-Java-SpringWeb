package com.nexuscore.webportal.repository;

import com.nexuscore.webportal.model.AppUser;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for the AppUser entity.
 * Provides standard CRUD operations and allows for custom query methods.
 */
@Repository
// Repository -> Uses JPA and Hibernate to connect to our required database and tables
public interface UserRepository extends JpaRepository<AppUser, Integer> {

    // Custom Queries
    
    /**
     * Checks if a user exists by matching exactly the provided username and password.
     * @param un The username.
     * @param pwd The raw password (Note: currently bypassing BCrypt if used directly).
     * @return true if exists, false otherwise.
     */
    boolean existsByUsernameAndPassword(String un, String pwd);
    
    /**
     * Retrieves a user wrapped in an Optional based on their username.
     * @param username The exact username.
     * @return Optional AppUser.
     */
    java.util.Optional<AppUser> findByUsername(String username);
}
