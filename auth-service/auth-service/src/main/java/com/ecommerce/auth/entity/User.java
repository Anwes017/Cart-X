package com.ecommerce.auth.entity;


import jakarta.persistence.*;
import lombok.*;
// Marks this class as a JPA Entity
// Hibernate will create a table for this class
@Entity
// Specifies the table name in the database
// Table name will be: users
@Table(name = "users")
// Lombok: generates getter methods for all fields
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Lombok: enables builder pattern for object creation
// Example:
// User user = User.builder().email("a@b.com").password("123").role("USER").build();
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role; // USER or ADMIN


}
