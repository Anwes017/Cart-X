package com.ecommerce.auth.repository;

import com.ecommerce.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/*
 This interface tells Spring:
 "Please generate database code for User table"
*/
public interface UserRepository extends JpaRepository<User, Long> {
    /*
     Used during:
     - Registration → check if email already exists
     - Login → fetch user by email

     Spring automatically creates SQL like:
     SELECT * FROM users WHERE email = ?
    */
    Optional<User> findByEmail(String email);
}
