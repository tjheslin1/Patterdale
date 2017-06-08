package io.github.tjheslin1.patterdale.http;

import io.github.tjheslin1.patterdale.WithMockito;
import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class NotFoundServletTest implements WithAssertions, WithMockito {

    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final PrintWriter writer = mock(PrintWriter.class);

    private final NotFoundServlet notFoundServlet = new NotFoundServlet();

    @Before
    public void setUp() throws IOException {
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void get404s() throws ServletException, IOException {
        notFoundServlet.doGet(null, response);

        verifyNotFoundPageDisplayed();
    }

    @Test
    public void delete404s() throws ServletException, IOException {
        notFoundServlet.doDelete(null, response);

        verifyNotFoundPageDisplayed();
    }

    @Test
    public void head404s() throws ServletException, IOException {
        notFoundServlet.doHead(null, response);

        verifyNotFoundPageDisplayed();
    }

    @Test
    public void options404s() throws ServletException, IOException {
        notFoundServlet.doOptions(null, response);

        verifyNotFoundPageDisplayed();
    }

    @Test
    public void post404s() throws ServletException, IOException {
        notFoundServlet.doPost(null, response);

        verifyNotFoundPageDisplayed();
    }

    @Test
    public void put404s() throws ServletException, IOException {
        notFoundServlet.doPut(null, response);

        verifyNotFoundPageDisplayed();
    }

    @Test
    public void trace404s() throws ServletException, IOException {
        notFoundServlet.doTrace(null, response);

        verifyNotFoundPageDisplayed();
    }

    private void verifyNotFoundPageDisplayed() {
        verify(response).setContentType("text/html");
        verify(response).setStatus(404);
        verify(writer).write("Not found!");
    }
}