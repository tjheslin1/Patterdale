package io.github.tjheslin1.patterdale.metrics;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import testutil.WithMockito;

import java.util.List;

import static io.github.tjheslin1.patterdale.metrics.ProbeResult.failure;
import static io.github.tjheslin1.patterdale.metrics.ProbeResult.success;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class MetricsUseCaseTest implements WithAssertions, WithMockito {

    @Test
    public void scrapeMetricsReturnsSuccess() throws Exception {

        List<SQLProbe> probes = singletonList(() -> success(""));

        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
        boolean result = metricsUseCase.scrapeMetrics();

        assertThat(result).isTrue();
    }

    @Test
    public void scrapeMetricsReturnsSuccessForMultipleProbes() throws Exception {

        List<SQLProbe> probes = asList(() -> success(""), () -> success(""));

        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
        boolean result = metricsUseCase.scrapeMetrics();

        assertThat(result).isTrue();
    }

    @Test
    public void scrapeMetricsReturnsFailureIfAnyProbeFails() throws Exception {

        List<SQLProbe> probes = asList(() -> success(""), () -> failure(""));

        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
        boolean result = metricsUseCase.scrapeMetrics();

        assertThat(result).isFalse();
    }

    @Test
    public void scrapeMetricsReturnsFailureIfAllProbeFails() throws Exception {

        List<SQLProbe> probes = asList(() -> failure(""), () -> failure(""));

        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
        boolean result = metricsUseCase.scrapeMetrics();

        assertThat(result).isFalse();
    }
}