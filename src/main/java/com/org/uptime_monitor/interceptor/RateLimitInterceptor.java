package com.org.uptime_monitor.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS_PER_MINUTE = 5;

    public RateLimitInterceptor(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception{

        String clientIP = request.getRemoteAddr();
        String redisKey = "rate_limit:" + clientIP;

        Long currentCount = redisTemplate.opsForValue().increment(redisKey);

        if(currentCount != null && currentCount == 1){
            redisTemplate.expire(redisKey, Duration.ofMinutes(1));
        }

        if(currentCount != null && currentCount > MAX_REQUESTS_PER_MINUTE){
            System.out.println("🚨 RATE LIMIT EXCEEDED FOR IP: " + clientIP);
            response.setStatus(429);
            response.getWriter().write("Too many requests. Please try again in a minute.");
            return false;
        }

        return true;
    }
}
