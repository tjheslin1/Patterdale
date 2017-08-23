package io.github.tjheslin1.patterdale.http;

import io.github.tjheslin1.patterdale.RuntimeParameters;
import org.junit.Ignore;
import testutil.WithMockito;
import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Ignore // TODO
public class MetricsServletTest implements WithAssertions, WithMockito {

    private final PrintWriter printerWriter = mock(PrintWriter.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final MetricsUseCase metricsUseCase = mock(MetricsUseCase.class);
    private final RuntimeParameters runtimeParameters = mock(RuntimeParameters.class);

    private final MetricsServlet metricsServlet = new MetricsServlet(metricsUseCase, runtimeParameters);

    @Before
    public void setup() throws Exception {
        when(response.getWriter()).thenReturn(printerWriter);
//        when(runtimeParameters.probes()).thenReturn("key=value");
    }

    @Test
    public void respondWithDatabaseMetricsOnSuccess() throws Exception {
//        when(metricsUseCase.scrapeMetrics()).thenReturn(true);

        metricsServlet.doGet(null, response);

        verify(metricsUseCase).scrapeMetrics();
        verify(printerWriter).print("database_up{key=value} 1");
    }

    @Test
    public void respondWithDatabaseMetricsErrorOnFailure() throws Exception {
//        when(metricsUseCase.scrapeMetrics()).thenReturn(false);

        metricsServlet.doGet(null, response);

        verify(metricsUseCase).scrapeMetrics();
        verify(response).setStatus(500);
        verify(printerWriter).print("database_up{key=value} 0");
    }
}