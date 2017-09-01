package io.github.tjheslin1.patterdale.http;

import io.github.tjheslin1.patterdale.RuntimeParameters;
import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeDefinition;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;
import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import testutil.WithMockito;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static io.github.tjheslin1.patterdale.metrics.probe.ProbeResult.failure;
import static io.github.tjheslin1.patterdale.metrics.probe.ProbeResult.success;
import static java.util.Arrays.asList;

public class MetricsServletTest implements WithAssertions, WithMockito {

    private static final ProbeDefinition PROBE_1 = new ProbeDefinition("SQL", "exists", "database_up", "key=value");
    private static final ProbeDefinition PROBE_2 = new ProbeDefinition("SQL", "exists", "database_other", "key=something");

    private static final ProbeResult PROBE_RESULT_1 = success("Success", PROBE_1);
    private static final ProbeResult PROBE_RESULT_2 = success("Success", PROBE_2);
    private static final ProbeResult FAILED_PROBE = failure("Failure", PROBE_2);

    private final PrintWriter printerWriter = mock(PrintWriter.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final MetricsUseCase metricsUseCase = mock(MetricsUseCase.class);
    private final RuntimeParameters runtimeParameters = mock(RuntimeParameters.class);
    private final Logger logger = mock(Logger.class);

    private final MetricsServlet metricsServlet = new MetricsServlet(metricsUseCase, logger);

    @Before
    public void setup() throws Exception {
        when(response.getWriter()).thenReturn(printerWriter);
        when(runtimeParameters.probes()).thenReturn(asList(PROBE_1, PROBE_2));
    }

    @Test
    public void respondWithDatabaseMetricsOnSuccess() throws Exception {
        when(metricsUseCase.scrapeMetrics()).thenReturn(asList(PROBE_RESULT_1, PROBE_RESULT_2));

        metricsServlet.doGet(null, response);

        verify(metricsUseCase).scrapeMetrics();
        verify(printerWriter).print("database_up{key=value} 1");
        verify(printerWriter).print("database_other{key=something} 1");
    }

    @Test
    public void respondWithDatabaseMetricsErrorOnFailure() throws Exception {
        when(metricsUseCase.scrapeMetrics()).thenReturn(asList(PROBE_RESULT_1, FAILED_PROBE));

        metricsServlet.doGet(null, response);

        verify(metricsUseCase).scrapeMetrics();
        verify(printerWriter).print("database_up{key=value} 1");
        verify(printerWriter).print("database_other{key=something} 0");
    }

    @Test
    public void logsErrorIfFailureOccursWritingToResponse() throws Exception {
//        IOException ioException = new IOException("ERR");
        when(response.getWriter()).thenThrow(IOException.class);
        when(metricsUseCase.scrapeMetrics()).thenReturn(asList(PROBE_RESULT_1, PROBE_RESULT_2));

        try {
            metricsServlet.doGet(null, response);
            fail("Expected an IOException to be caught.");
        } catch (IOException e) {
            verify(logger).error("IO error occurred writing to /metrics page.", e);
        }
    }
}