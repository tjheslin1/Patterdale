package io.github.tjheslin1.patterdale.http;

import testutil.WithMockito;
import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class MetricsServletTest implements WithAssertions, WithMockito {

    private final PrintWriter printerWriter = mock(PrintWriter.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final MetricsUseCase metricsUseCase = mock(MetricsUseCase.class);

    private final MetricsServlet metricsServlet = new MetricsServlet(metricsUseCase);

    @Before
    public void setup() throws Exception {
        when(response.getWriter()).thenReturn(printerWriter);
    }

    @Test
    public void respondWithDatabaseMetricsOnSuccess() throws Exception {
        when(metricsUseCase.scrapeMetrics()).thenReturn(true);

        metricsServlet.doGet(null, response);

        verify(metricsUseCase).scrapeMetrics();
        verify(printerWriter).print("oracle_health_check 1");
    }

    @Test
    public void respondWithDatabaseMetricsErrorOnFailure() throws Exception {
        when(metricsUseCase.scrapeMetrics()).thenReturn(false);

        metricsServlet.doGet(null, response);

        verify(metricsUseCase).scrapeMetrics();
        verify(response).setStatus(500);
        verify(printerWriter).print("oracle_health_check 0");
    }
}