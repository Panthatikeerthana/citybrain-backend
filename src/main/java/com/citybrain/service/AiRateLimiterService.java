package com.citybrain.service;

import com.citybrain.exception.RateLimitExceededException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AiRateLimiterService {

    private static final int MAX_REQUESTS_PER_MINUTE = 15;
    private final ConcurrentHashMap<String, UserWindow> requestCounts = new ConcurrentHashMap<>();

    private static class UserWindow {
        final long windowStartMs;
        final AtomicInteger count;

        UserWindow(long windowStartMs) {
            this.windowStartMs = windowStartMs;
            this.count = new AtomicInteger(1);
        }
    }

    public void checkRateLimit(String userKey) {
        if (userKey == null || userKey.isBlank()) {
            userKey = "anonymous";
        }

        final String key = userKey;
        long now = System.currentTimeMillis();

        requestCounts.compute(key, (k, current) -> {
            if (current == null || (now - current.windowStartMs) > 60_000) {
                return new UserWindow(now);
            }
            if (current.count.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
                throw new RateLimitExceededException(
                    "Rate limit exceeded: Maximum " + MAX_REQUESTS_PER_MINUTE + " AI requests per minute allowed."
                );
            }
            return current;
        });
    }

    // Allows manual clearing or testing
    public void reset(String userKey) {
        requestCounts.remove(userKey);
    }
}
