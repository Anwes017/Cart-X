package com.ecommerce.apigateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    // ================= AUTH SERVICE =================
    @RequestMapping(
            value = "/fallback/auth",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<String> authServiceFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just("""
                {
                  "service": "AUTH",
                  "message": "Auth service is currently unavailable. Please try again later."
                }
                """);
    }

    // ================= PRODUCT SERVICE =================
    @RequestMapping(
            value = "/fallback/product",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<String> productServiceFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just("""
                {
                  "service": "PRODUCT",
                  "message": "Product service is unavailable. Please try again later."
                }
                """);
    }

    // ================= ORDER SERVICE =================
    @RequestMapping(
            value = "/fallback/order",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<String> orderServiceFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just("""
                {
                  "service": "ORDER",
                  "message": "Order service is unavailable. Please try again later."
                }
                """);
    }

    // ================= PAYMENT SERVICE =================
    @RequestMapping(
            value = "/fallback/payment",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<String> paymentServiceFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just("""
                {
                  "service": "PAYMENT",
                  "message": "Payment service is unavailable. Please try again later."
                }
                """);
    }

    // ================= NOTIFICATION SERVICE =================
    @RequestMapping(
            value = "/fallback/notification",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<String> notificationServiceFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just("""
                {
                  "service": "NOTIFICATION",
                  "message": "Notification service is unavailable. Please try again later."
                }
                """);
    }
}
