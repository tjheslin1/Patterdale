package io.github.tjheslin1.patterdale.http;

import io.github.tjheslin1.patterdale.config.RuntimeParameters;
import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition;
import io.github.tjheslin1.patterdale.metrics.probe.Probe;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;
import io.prometheus.client.CollectorRegistry;
import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import testutil.WithMockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition.databaseDefinition;
import static io.github.tjheslin1.patterdale.metrics.probe.Probe.probe;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.mockito.Mockito.times;

public class MetricsServletTest implements WithAssertions, WithMockito {

    private static final Probe PROBE_1 = probe("name", "SQL", "exists", "database_up", "key=\"value\"");
    private static final Probe PROBE_2 = probe("name2", "SQL", "exists", "database_other", "key=\"something\"");
    private static final Probe PROBE_3 = probe("name3", "SQL", "exists", "database_list", "key=\"somethingElse\",result=\"%s\"");

    private static final DatabaseDefinition DATABASE_DEFINITION = databaseDefinition("", "", "", asList(PROBE_1.name, PROBE_2.name));

    private static final ProbeResult PROBE_RESULT_1 = new ProbeResult(1, PROBE_1);
    private static final ProbeResult PROBE_RESULT_2 = new ProbeResult(1, PROBE_2);
    private static final ProbeResult PROBE_RESULT_3 = new ProbeResult(4.5, PROBE_3, singletonList("example SQL"));
    private static final ProbeResult FAILED_PROBE = new ProbeResult(0, PROBE_2);

    private final PrintWriter printerWriter = mock(PrintWriter.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final CollectorRegistry registry = mock(CollectorRegistry.class);
    private final MetricsUseCase metricsUseCase = mock(MetricsUseCase.class);
    private final RuntimeParameters runtimeParameters = mock(RuntimeParameters.class);
    private final Logger logger = mock(Logger.class);

    private final MetricsServlet metricsServlet = new MetricsServlet(registry, metricsUseCase, logger, 60);

    @Before
    public void setup() throws Exception {
        when(response.getWriter()).thenReturn(printerWriter);
        when(runtimeParameters.databases()).thenReturn(singletonList(DATABASE_DEFINITION));
        when(request.getParameterValues("name[]")).thenReturn(new String[]{});
        when(registry.filteredMetricFamilySamples(emptySet())).thenReturn(emptyEnumeration());
    }

    @Test
    public void respondWithDatabaseMetricsOnSuccess() throws Exception {
        when(metricsUseCase.scrapeMetrics()).thenReturn(asList(PROBE_RESULT_1, PROBE_RESULT_2, PROBE_RESULT_3));

        metricsServlet.doGet(request, response);

        verify(metricsUseCase).scrapeMetrics();
        verify(printerWriter).println("database_up{key=\"value\"} 1.0");
        verify(printerWriter).println("database_other{key=\"something\"} 1.0");
        verify(printerWriter).println("database_list{key=\"somethingElse\",result=\"example SQL\"} 4.5");
    }

    @Test
    public void respondWithDatabaseMetricsErrorOnFailure() throws Exception {
        when(metricsUseCase.scrapeMetrics()).thenReturn(asList(PROBE_RESULT_1, FAILED_PROBE));

        metricsServlet.doGet(request, response);

        verify(metricsUseCase).scrapeMetrics();
        verify(printerWriter).println("database_up{key=\"value\"} 1.0");
        verify(printerWriter).println("database_other{key=\"something\"} 0.0");
    }

    @Test
    public void metricsResultsAreCachedForAConfiguredDuration() throws Exception {
        when(metricsUseCase.scrapeMetrics()).thenReturn(singletonList(PROBE_RESULT_1));

        metricsServlet.doGet(request, response);
        metricsServlet.doGet(request, response);

        verify(metricsUseCase, times(1)).scrapeMetrics();
    }
}