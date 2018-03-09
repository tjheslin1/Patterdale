package io.github.tjheslin1.patterdale.metrics;

import io.github.tjheslin1.patterdale.config.RuntimeParameters;
import io.github.tjheslin1.patterdale.metrics.probe.OracleSQLProbe;
import io.github.tjheslin1.patterdale.metrics.probe.Probe;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;
import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Test;
import testutil.WithMockito;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MetricsUseCaseTest implements WithAssertions, WithMockito {

    private final RuntimeParameters runtimeParameters = mock(RuntimeParameters.class);
    private final ExecutorService executorService = mock(ExecutorService.class);
    private final OracleSQLProbe sqlProbe1 = mock(OracleSQLProbe.class);
    private final OracleSQLProbe sqlProbe2 = mock(OracleSQLProbe.class);
    private final Probe probe1 = mock(Probe.class);
    private final Probe probe2 = mock(Probe.class);
    private final ProbeResult probeResult1 = mock(ProbeResult.class);
    private final ProbeResult probeResult2 = mock(ProbeResult.class);
    private final ProbeResult probeResult3 = mock(ProbeResult.class);
    private final Future<List<ProbeResult>> future1 = mock(Future.class);
    private final Future<List<ProbeResult>> future2 = mock(Future.class);

    private final MetricsUseCase metricsUseCase = new MetricsUseCase(asList(sqlProbe1, sqlProbe2), runtimeParameters, () -> executorService);

    @Before
    public void setUp() throws Exception {
        when(future1.get(1, SECONDS)).thenReturn(singletonList(probeResult1));
        when(future2.get(1, SECONDS)).thenReturn(asList(probeResult2, probeResult3));
        when(executorService.submit(new ScrapeProbe(sqlProbe1))).thenReturn(future1);
        when(executorService.submit(new ScrapeProbe(sqlProbe2))).thenReturn(future2);
    }

    @Test
    public void scrapeMetricsCollatesProbeResults() {
        final List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();

        assertThat(probeResults).containsExactlyInAnyOrder(probeResult1, probeResult2, probeResult3);
    }

    @Test
    public void scrapeMetricsShutsDownExecutorService() {
        metricsUseCase.scrapeMetrics();

        verify(executorService).shutdown();
    }

    @Test
    public void scrapeMetricsAwaitsTerminationOfExecutorService() throws InterruptedException {
        when(runtimeParameters.probeConnectionWaitInSeconds()).thenReturn(10);

        metricsUseCase.scrapeMetrics();

        verify(executorService).awaitTermination(10, SECONDS);
    }

    @Test
    public void scrapeMetricsReturnsFailedProbeResultsIfInterruptedExceptionIsThrown() throws InterruptedException {
        when(sqlProbe1.probeDefinition()).thenReturn(probe1);
        when(sqlProbe2.probeDefinition()).thenReturn(probe2);
        when(executorService.awaitTermination(anyLong(), any())).thenThrow(new InterruptedException());

        final List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();

        assertThat(probeResults).containsExactlyInAnyOrder(
                ProbeResult.failedProbe(probe1),
                ProbeResult.failedProbe(probe2)
        );
    }

    @Test
    public void scrapeMetricsReturnsNoResultsFromProbeIfFutureTimesOut() throws Exception {
        when(future1.get(1, SECONDS)).thenThrow(new TimeoutException());

        final List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();

        assertThat(probeResults).containsExactlyInAnyOrder(probeResult2, probeResult3);
    }
}