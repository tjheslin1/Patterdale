package io.github.tjheslin1.patterdale.http;

import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.lang.String.format;

public class MetricsCache implements Supplier<List<ProbeResult>>, Runnable {
    private final AtomicReference<List<ProbeResult>> results = new AtomicReference<>();
    private final MetricsUseCase metricsUseCase;

    private MetricsCache(MetricsUseCase metricsUseCase) {
        this.metricsUseCase = metricsUseCase;
    }

    public static MetricsCache metricsCache(MetricsUseCase metricsUseCase, long cacheDuration) {
        if (cacheDuration <= 0) {
            throw new IllegalArgumentException(format("Cache duration must be positive but was '%d'!", cacheDuration));
        }

        MetricsCache metricsCache = new MetricsCache(metricsUseCase);
        metricsCache.run();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(metricsCache, cacheDuration, cacheDuration, TimeUnit.SECONDS);
        return metricsCache;
    }

    @Override
    public List<ProbeResult> get() {
        return results.get();
    }

    @Override
    public void run() {
        results.set(metricsUseCase.scrapeMetrics());
    }
}
