package com.example.takehome.ratelimit;

import com.example.takehome.security.user.User;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.StringTokenizer;

@Component
public class RequestFilter extends OncePerRequestFilter {

    @Autowired
    RateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if(request.getRequestURI().startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
        } else {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Bucket bucket;
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                final String email = ((User)authentication.getPrincipal()).getEmail();
                bucket = rateLimiter.resolveBucketForAuthenticatedUsers(email);
            } else {
                final String ip = this.getClientIP(request);
                bucket = rateLimiter.resolveBucketForAnonymousUsers(ip);
            }
            if(bucket.tryConsume(1)) {
                filterChain.doFilter(request, response);
            }
            else {
                sendErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS.value());
            }
        }
    }

    private String getClientIP(final HttpServletRequest request) {
        final String xForwardedForHeader = request.getHeader("X-FORWARDED-FOR");
        return
            Optional.ofNullable(xForwardedForHeader)
                .filter(ip -> !ip.isEmpty())
                .map(ip -> new StringTokenizer(ip, ",").nextToken().trim())
                .orElseGet(request::getRemoteAddr);
    }

    private void sendErrorResponse(HttpServletResponse response, int value) {
        response.setStatus(value);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

}
