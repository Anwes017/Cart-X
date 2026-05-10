package com.ecommerce.apigateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RedisRateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;

    private static final int MAX_REQUESTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    public RedisRateLimitFilter(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String ip = exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();

        String key = "rate_limit:" + ip;

        return redisTemplate.opsForValue()
                .increment(key)
                .flatMap(count -> {

                    if (count == 1) {
                        redisTemplate.expire(key, WINDOW).subscribe();
                    }

                    if (count > MAX_REQUESTS) {
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        return exchange.getResponse().setComplete();
                    }

                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return -1; // VERY IMPORTANT → run early
    }
}
