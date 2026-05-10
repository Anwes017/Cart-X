package com.ecommerce.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class GatewayConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http
                // ❌ Disable CSRF for APIs
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // ❌ Disable default login pages
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                // ✅ Authorization rules
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/**").permitAll()      // 👈 allow auth APIs
                        .pathMatchers("/actuator/**").permitAll()  // 👈 allow health check
                        .pathMatchers("/products/**").permitAll()
                        .pathMatchers("/cart/**").permitAll()
                        .pathMatchers("/orders/**").permitAll()
                        .pathMatchers("/payments/**").permitAll()
                        .anyExchange().authenticated()             // 🔒 everything else secured
                );

        return http.build();
    }
}
