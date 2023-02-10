package com.example.takehome.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class RateLimiter {

    @Value("${ratelimit.refresh.interval}")
    private long ratelimitRefreshInterval;

    @Value("${ratelimit.request.authenticated}")
    private long ratelimitRequestAuthenticated;

    @Value("${ratelimit.request.anonymous}")
    private long ratelimitRequestAnonymous;

    @Autowired
    ProxyManager<String> proxyManager;

    public Bucket resolveBucketForAuthenticatedUsers(String key) {
        final Supplier<BucketConfiguration> configSupplier = getConfigSupplierForUser(ratelimitRequestAuthenticated);
        return this.getProxyManager(key, configSupplier);
    }

    public Bucket resolveBucketForAnonymousUsers(String key) {
        final Supplier<BucketConfiguration> configSupplier = getConfigSupplierForUser(ratelimitRequestAnonymous);
        return this.getProxyManager(key, configSupplier);
    }

    private Bucket getProxyManager(final String key, final Supplier<BucketConfiguration> configSupplier) {
        return proxyManager.builder().build(key, configSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplierForUser(final long tokens) {
        final Refill refill = Refill.intervally(tokens, Duration.ofSeconds(ratelimitRefreshInterval));
        final Bandwidth limit = Bandwidth.classic(tokens, refill);
        return () -> (BucketConfiguration.builder()
            .addLimit(limit)
            .build());
    }

}
