package com.ecommerce.product.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter filter;

    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/products/internal/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        http.addFilterBefore(
                filter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
//package com.ecommerce.product.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.*;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig {
//
////    private final JwtAuthFilter filter;
//
//    @Bean
//    public SecurityFilterChain security(HttpSecurity http) throws Exception {
//
//        http.csrf(csrf -> csrf.disable());
//
//        http.authorizeHttpRequests(auth -> auth
//                // ✅ Anyone logged in can VIEW products
//                .requestMatchers(HttpMethod.GET, "/products/**").authenticated()
//
//                // 🔒 Only ADMIN can modify products
//                .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
//
//                // Everything else must be authenticated
//                .anyRequest().authenticated()
//        );
//
//        http.addFilterBefore(filter,
//                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}
