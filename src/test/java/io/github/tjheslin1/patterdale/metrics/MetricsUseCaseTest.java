package io.github.tjheslin1.patterdale.metrics;

import org.assertj.core.api.WithAssertions;
import org.junit.Ignore;
import org.junit.Test;
import testutil.WithMockito;

import java.util.List;
import java.util.Map;

import static io.github.tjheslin1.patterdale.metrics.ProbeResult.failure;
import static io.github.tjheslin1.patterdale.metrics.ProbeResult.success;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Ignore
public class MetricsUseCaseTest implements WithAssertions, WithMockito {

    @Test
    public void scrapeMetricsReturnsSuccess() throws Exception {
//        List<OracleSQLProbe> probes = singletonList(() -> success(""));

//        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
//        Map<ProbeDefinition, ProbeResult> probeResults = metricsUseCase.scrapeMetrics();
//
//        assertThat(probeResults).isEqualTo(singletonList(new ProbeResult(true, "")));
    }

    @Test
    public void scrapeMetricsReturnsSuccessForMultipleProbes() throws Exception {
//        List<OracleSQLProbe> probes = asList(() -> success(""), () -> success(""));
//
//        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
//        List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();
//
//        assertThat(probeResults).isEqualTo(asList(
//                new ProbeResult(true, ""),
//                new ProbeResult(true, "")
//        ));
    }

    @Test
    public void scrapeMetricsReturnsFailureIfAnyProbeFails() throws Exception {
//        List<OracleSQLProbe> probes = asList(() -> success(""), () -> failure(""));
//
//        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
//        List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();
//
//        assertThat(probeResults).isEqualTo(asList(
//                new ProbeResult(true, ""),
//                new ProbeResult(false, "")
//        ));
    }

    @Test
    public void scrapeMetricsReturnsFailureIfAllProbeFails() throws Exception {
//        List<OracleSQLProbe> probes = asList(() -> failure(""), () -> failure(""));
//
//        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
//        List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();
//
//        assertThat(probeResults).isEqualTo(asList(
//                new ProbeResult(false, ""),
//                new ProbeResult(true, "")
//        ));
    }
}