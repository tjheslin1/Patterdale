package io.github.tjheslin1.patterdale.http;

import com.google.common.base.Stopwatch;
import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.github.tjheslin1.patterdale.http.MetricsCache.metricsCache;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class MetricsCacheTest implements WithAssertions {

    private final MetricsUseCase metricsUseCase = mock(MetricsUseCase.class);

    @Test
    public void cacheDurationMustBePositive() {
        assertThatThrownBy(() -> metricsCache(mock(MetricsUseCase.class), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cache duration must be positive but was '0'!");
    }

    @Test
    public void initialValueIsAlreadyPopulated() {
        List<ProbeResult> probeResults = singletonList(mock(ProbeResult.class));
        when(metricsUseCase.scrapeMetrics()).thenReturn(probeResults);

        MetricsCache metricsCache = metricsCache(metricsUseCase, 1);

        assertThat(metricsCache.get()).isSameAs(probeResults);
    }

    @Test
    public void cacheIsReloadedPeriodically() throws InterruptedException {
        List<ProbeResult> initial = singletonList(mock(ProbeResult.class));
        List<ProbeResult> updated = singletonList(mock(ProbeResult.class));
        when(metricsUseCase.scrapeMetrics()).thenReturn(initial, updated);

        MetricsCache metricsCache = metricsCache(metricsUseCase, 1);

        SECONDS.sleep(2);

        assertThat(metricsCache.get()).isSameAs(updated);
    }

    @Test
    public void cacheDoesNotBlockSinceItReloadsInABackgroundThread() {
        MetricsCache metricsCache = metricsCache(metricsUseCase, 1);

        when(metricsUseCase.scrapeMetrics()).thenAnswer(invocation -> {
            Thread.sleep(5000);
            return null;
        });

        Stopwatch stopwatch = Stopwatch.createStarted();

        metricsCache.get();

        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        assertThat(elapsed).isLessThan(100);
    }
}