package io.github.tjheslin1.patterdale.metrics;

import io.github.tjheslin1.patterdale.config.RuntimeParameters;
import io.github.tjheslin1.patterdale.metrics.probe.OracleSQLProbe;
import io.github.tjheslin1.patterdale.metrics.probe.Probe;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;
import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import testutil.WithMockito;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static io.github.tjheslin1.patterdale.metrics.probe.ProbeResult.failedProbe;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MetricsUseCaseTest implements WithAssertions, WithMockito {

    private static final int TIMEOUT = 10;
    private final RuntimeParameters runtimeParameters = mock(RuntimeParameters.class);
    private final ExecutorService executorService = mock(ExecutorService.class);
    private final OracleSQLProbe sqlProbe1 = mock(OracleSQLProbe.class);
    private final OracleSQLProbe sqlProbe2 = mock(OracleSQLProbe.class);
    private final OracleSQLProbe sqlProbe3 = mock(OracleSQLProbe.class);
    private final Probe probe1 = mock(Probe.class);
    private final Probe probe2 = mock(Probe.class);
    private final Probe probe3 = mock(Probe.class);
    private final Probe probe4 = mock(Probe.class);
    private final ProbeResult probeResult1 = mock(ProbeResult.class);
    private final ProbeResult probeResult2 = mock(ProbeResult.class);
    private final ProbeResult probeResult3 = mock(ProbeResult.class);
    private final Future<List<ProbeResult>> future1 = mock(Future.class);
    private final Future<List<ProbeResult>> future2 = mock(Future.class);
    private final Future<List<ProbeResult>> future3 = mock(Future.class);

    private final MetricsUseCase metricsUseCase = new MetricsUseCase(mock(Logger.class), asList(sqlProbe1, sqlProbe2), runtimeParameters, () -> executorService);

    @Before
    public void setUp() throws Exception {
        when(runtimeParameters.probeConnectionWaitInSeconds()).thenReturn(TIMEOUT);
        when(sqlProbe1.probeDefinition()).thenReturn(probe1);
        when(sqlProbe2.probeDefinition()).thenReturn(probe2);
        when(future1.get(anyLong(), eq(SECONDS))).thenReturn(singletonList(probeResult1));
        when(future2.get(anyLong(), eq(SECONDS))).thenReturn(asList(probeResult2, probeResult3));
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
    public void scrapeMetricsReturnsFailedProbeResultsOnlyForThoseWhereAnInterruptedExceptionIsThrown() throws InterruptedException, TimeoutException, ExecutionException {
        MetricsUseCase metricsUseCase = new MetricsUseCase(mock(Logger.class), asList(sqlProbe1, sqlProbe2, sqlProbe3), runtimeParameters, () -> executorService);
        when(sqlProbe3.probeDefinition()).thenReturn(probe3);
        when(future1.get(anyLong(), eq(SECONDS))).thenThrow(new InterruptedException());
        ProbeResult successfulProbe = new ProbeResult(1.0, probe4);
        when(future2.get(anyLong(), eq(SECONDS))).thenReturn(singletonList(successfulProbe));
        when(future3.get(anyLong(), eq(SECONDS))).thenThrow(new InterruptedException());

        when(executorService.submit(new ScrapeProbe(sqlProbe3))).thenReturn(future3);

        final List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();

        assertThat(probeResults).containsExactlyInAnyOrder(
                failedProbe(probe1),
                successfulProbe,
                failedProbe(probe3)
        );
    }

    @Test
    public void scrapeMetricsReturnsFailedResultsFromProbeIfFutureTimesOut() throws Exception {
        when(future1.get(anyLong(), eq(SECONDS))).thenThrow(new TimeoutException());

        final List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();

        assertThat(probeResults).containsExactlyInAnyOrder(failedProbe(probe1), probeResult2, probeResult3);
    }
}