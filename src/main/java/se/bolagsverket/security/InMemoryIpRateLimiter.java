package se.bolagsverket.security;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiter that tracks requests per IP address.
 * Each IP is allowed a maximum number of requests within a time window.
 * Once the window expires, the counter resets.
 *
 * If too many unique IPs are tracked, all counters are cleared.
 */
public final class InMemoryIpRateLimiter {

    private static final class WindowCounter {
        long windowId;
        int count;

        WindowCounter(long windowId, int count) {
            this.windowId = windowId;
            this.count = count;
        }
    }

    private final ConcurrentHashMap<String, WindowCounter> countersByIp = new ConcurrentHashMap<>();

    private final int maxRequestsPerWindow;
    private final int windowSeconds;
    private final int maxIpsRemembered;

    public InMemoryIpRateLimiter(int maxRequestsPerWindow, int windowSeconds, int maxIpsRemembered) {
        if (maxRequestsPerWindow <= 0) throw new IllegalArgumentException("maxRequestsPerWindow must be > 0");
        if (windowSeconds <= 0) throw new IllegalArgumentException("windowSeconds must be > 0");
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowSeconds = windowSeconds;
        this.maxIpsRemembered = Math.max(64, maxIpsRemembered);
    }

    /**
     * Returns true if the request is allowed, false if rate limited (429).
     */
    public boolean isRequestAllow(String ip) {
        long currentWindowId = (System.currentTimeMillis() / 1000L) / windowSeconds;

        if (countersByIp.size() >= maxIpsRemembered) {
            countersByIp.clear();
        }

        final boolean[] allowed = new boolean[1];

        countersByIp.compute(ip, (key, existing) -> {
            if (existing == null || existing.windowId != currentWindowId) {
                allowed[0] = true;
                return new WindowCounter(currentWindowId, 1);
            }

            existing.count++;
            allowed[0] = existing.count <= maxRequestsPerWindow;
            return existing;
        });

        return allowed[0];
    }
}