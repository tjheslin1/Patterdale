package io.github.tjheslin1.patterdale.http;

import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Test;
import testutil.WithMockito;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class StatusServletTest implements WithAssertions, WithMockito {

    private final PrintWriter printerWriter = mock(PrintWriter.class);
    private final HttpServletResponse resp = mock(HttpServletResponse.class);

    private final StatusServlet readyServlet = new StatusServlet();

    @Before
    public void setup() throws Exception {
        when(resp.getWriter()).thenReturn(printerWriter);
        System.setProperty("config.file", "src/test/resources/patterdale.yml");
    }

    @Test
    public void successfulReadyProbeReturnsOk() throws Exception {
        readyServlet.doGet(null, resp);

        verify(printerWriter).println("httpPort: 7001\n" +
                "cacheDuration: 60\n" +
                "databases:\n" +
                "  - name: bobsDatabase\n" +
                "    user: system\n" +
                "    jdbcUrl: jdbc:oracle:thin:@localhost:1522:xe\n" +
                "    probes:\n" +
                "      - healthCheck\n" +
                "  - name: alicesDatabase\n" +
                "    user: system\n" +
                "    jdbcUrl: jdbc:oracle:thin:@localhost:1523:xe\n" +
                "    probes:\n" +
                "      - healthCheck\n" +
                "      - slowestQueries\n" +
                "\n" +
                "connectionPool:\n" +
                "  maxSize: 5\n" +
                "  minIdle: 1\n" +
                "  maxConnectionRetries: 10\n" +
                "  connectionRetryDelayInSeconds: 60\n" +
                "\n" +
                "probes:\n" +
                "  - name: healthCheck\n" +
                "    type: exists\n" +
                "    query: SELECT 1 FROM DUAL\n" +
                "    metricName: database_up\n" +
                "    metricLabels: query=\"SELECT 1 FROM DUAL\"\n" +
                "  - name: slowestQueries\n" +
                "    type: list\n" +
                "    metricName: slowest_queries\n" +
                "    metricLabels: sqlText=\"%s\",sqlId=\"%s\",username=\"%s\",childNumber=\"%s\",diskReads=\"%s\",executions=\"%s\",firstLoadTime=\"%s\",lastLoadTime=\"%s\"\n" +
                "    query: |\n" +
                "        SELECT * FROM\n" +
                "        (SELECT\n" +
                "            s.elapsed_time / s.executions / 1000 AS AVG_ELAPSED_TIME_IN_MILLIS,\n" +
                "            SUBSTR(s.sql_fulltext, 1, 80) AS SQL_TEXT,\n" +
                "            s.sql_id,\n" +
                "            d.username,\n" +
                "            s.child_number,\n" +
                "            s.disk_reads,\n" +
                "            s.executions,\n" +
                "            s.first_load_time,\n" +
                "            s.last_load_time\n" +
                "        FROM    v$sql s, dba_users d\n" +
                "        WHERE   s.parsing_user_id = d.user_id\n" +
                "        AND trunc(TO_DATE(s.last_load_time, 'YYYY-MM-DD/HH24:MI:SS')) >= trunc(SYSDATE - 1)\n" +
                "        ORDER BY elapsed_time DESC)\n" +
                "        WHERE ROWNUM <= 5;");
    }
}