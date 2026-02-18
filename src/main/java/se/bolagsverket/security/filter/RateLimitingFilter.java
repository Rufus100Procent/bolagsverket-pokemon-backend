package se.bolagsverket.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import se.bolagsverket.security.InMemoryIpRateLimiter;

import java.io.IOException;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final InMemoryIpRateLimiter limiter;

    public RateLimitingFilter(InMemoryIpRateLimiter limiter) {
        this.limiter = limiter;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws IOException, ServletException {

        String clientIp = resolveClientIp(request);

        if (clientIp == null || clientIp.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Bad client IP");
            return;
        }

        if (!limiter.isRequestAllow(clientIp)) {
            response.setStatus(429);
            response.setHeader("Rate-Limited", "true");
            response.getWriter().write("Too Many Requests");
            return;
        }

        response.setHeader("Rate-Limited", "false");
        chain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
