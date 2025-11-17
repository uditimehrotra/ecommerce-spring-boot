package com.ecommerce.platform.repository;

import com.ecommerce.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Marks this as a Spring repository bean
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Spring Security will use this method to load a user by their username.
     * Spring Data JPA is smart enough to automatically create the query
     * for us just based on the method name "findByUsername".
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found, or an empty Optional if not.
     */
    Optional<User> findByUsername(String username);
}