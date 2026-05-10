package com.ecommerce.apigateway.security;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final String[] PUBLIC_PATHS = {
            "/auth",
            "/actuator",
            "/swagger",
            "/v3/api-docs",
            "/products"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // ✅ Public routes
        for (String p : PUBLIC_PATHS) {
            if (path.startsWith(p)) {
                return chain.filter(exchange);
            }
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        // ✅ ONLY token validation
        if (!jwtUtil.isTokenValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String userId = jwtUtil.extractUsername(token);
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.header("X-User-Id", userId))
                .build();

        return chain.filter(mutatedExchange);
    }
}

//@Component
//public class JwtAuthFilter implements WebFilter {
//
//    private final JwtUtil jwtUtil;
//
//    // ✅ CENTRAL PUBLIC ROUTE LIST
//    private static final String[] PUBLIC_PATHS = {
//            "/auth",
//            "/actuator/health",
//            "/fallback",
//            "/swagger",
//            "/v3/api-docs"
//    };
//
//    public JwtAuthFilter(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//
//        String path = exchange.getRequest().getURI().getPath();
//
//        // ✅ PUBLIC ROUTES → SKIP JWT
//        if (isPublicPath(path)) {
//            return chain.filter(exchange);
//        }
//
//        // 🔒 PROTECTED ROUTES → JWT REQUIRED
//        String authHeader = exchange.getRequest()
//                .getHeaders()
//                .getFirst(HttpHeaders.AUTHORIZATION);
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        String token = authHeader.substring(7);
//
//        if (!jwtUtil.isTokenValid(token)) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        return chain.filter(exchange);
//    }
//
//    private boolean isPublicPath(String path) {
//        for (String publicPath : PUBLIC_PATHS) {
//            if (path.startsWith(publicPath)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
