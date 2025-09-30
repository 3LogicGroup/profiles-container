package ru.graviton.profiles.service.licenses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final ConcurrentHashMap<String, RateLimitEntry> rateLimitMap = new ConcurrentHashMap<>();

    public boolean isAllowed(String key, String action, int maxAttempts, Duration timeWindow) {
        String rateLimitKey = key + ":" + action;
        RateLimitEntry entry = rateLimitMap.computeIfAbsent(rateLimitKey,
                k -> new RateLimitEntry(LocalDateTime.now(), new AtomicInteger(0)));

        LocalDateTime now = LocalDateTime.now();

        // Если окно истекло, сбрасываем счетчик
        if (now.isAfter(entry.getWindowStart().plus(timeWindow))) {
            entry.setWindowStart(now);
            entry.getAttempts().set(0);
        }

        int currentAttempts = entry.getAttempts().incrementAndGet();
        return currentAttempts <= maxAttempts;
    }

    public void recordAttempt(String key, String action) {
        // Уже записывается в isAllowed
    }

    public void reset(String key, String action) {
        String rateLimitKey = key + ":" + action;
        rateLimitMap.remove(rateLimitKey);
    }

    @Getter
    private static class RateLimitEntry {
        @Setter
        private LocalDateTime windowStart;
        private final AtomicInteger attempts;

        public RateLimitEntry(LocalDateTime windowStart, AtomicInteger attempts) {
            this.windowStart = windowStart;
            this.attempts = attempts;
        }

    }
}