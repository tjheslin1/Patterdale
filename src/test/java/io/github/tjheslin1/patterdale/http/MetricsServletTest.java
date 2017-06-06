package io.github.tjheslin1.patterdale.http;

import io.github.tjheslin1.patterdale.WithMockito;
import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Ignore;
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

    @Ignore
    @Test
    public void respondWithDatabaseMetrics() throws Exception {
        metricsServlet.doGet(null, response);

        verify(metricsUseCase).scrapeMetrics();
        verify(response).setStatus(200);
        verify(printerWriter).print("scrapeMetrics....");
    }
}