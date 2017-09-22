package io.github.tjheslin1.patterdale.http;

import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Test;
import testutil.WithMockito;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class ReadyServletTest implements WithAssertions, WithMockito {

    private final PrintWriter printerWriter = mock(PrintWriter.class);
    private final HttpServletResponse resp = mock(HttpServletResponse.class);

    private final ReadyServlet readyServlet = new ReadyServlet();


    @Before
    public void setup() throws Exception {
        when(resp.getWriter()).thenReturn(printerWriter);
    }

    @Test
    public void successfulReadyProbeReturnsOk() throws Exception {
        readyServlet.doGet(null, resp);

        verify(printerWriter).println("OK");
    }
}