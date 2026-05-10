package com.ecommerce.apigateway.ratelimit;

import com.ecommerce.apigateway.security.JwtUtil;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("jwtUserKeyResolver")
public class JwtUserKeyResolver implements KeyResolver {

    private final JwtUtil jwtUtil;

    public JwtUserKeyResolver(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<String> resolve(org.springframework.web.server.ServerWebExchange exchange) {

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.just("anonymous");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            return Mono.just("invalid");
        }

        return Mono.just("user:" + jwtUtil.extractUsername(token));
    }
}