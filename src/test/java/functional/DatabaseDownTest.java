package functional;

import io.github.tjheslin1.patterdale.Patterdale;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.WithAssertions;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import static testutil.TestUtil.responseBody;
import static testutil.TestUtil.startPatterdale;

public class DatabaseDownTest implements WithAssertions {

    private static final int H2_PORT = 9292;

    private static Patterdale patterdale;
    private static Server h2;

    @Test
    public void scrapesOracleDatabaseMetricsOnRequest() throws Exception {
        assertDatabaseMetric("1.0");

        h2.stop();

        Thread.sleep(5000);

        assertDatabaseMetric("-1.0");
    }

    private void assertDatabaseMetric(final String databaseUp) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(new HttpGet("http://localhost:7001/metrics"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        String responseBody = responseBody(response);
        assertThat(responseBody).matches(Pattern.compile(
                "database_up\\{database=\"bobsDatabase\",label=\"value\",query=\"SELECT 1 FROM DUAL\"} " + databaseUp + "\n" +
                        "database_up\\{database=\"alicesDatabase\",query=\"SELECT 1 FROM DUAL\"} 1.0\n" +
                        ".*"
                , Pattern.DOTALL)
        );

        // the order of the jvm and jetty metrics changes
        assertThat(responseBody).matches(Pattern.compile(".*jvm.*\\{.*}.*", Pattern.DOTALL));
        assertThat(responseBody).matches(Pattern.compile(".*jetty.*\\{.*}.*", Pattern.DOTALL));
    }

    @BeforeClass
    public static void setUp() throws SQLException {
        h2 = Server.createTcpServer("-tcpPort", String.valueOf(H2_PORT)).start();
        patterdale = startPatterdale("src/test/resources/patterdale-proxy-h2.yml", "src/test/resources/passwords-h2.yml");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (patterdale != null) {
            patterdale.stop();
        }

        h2.shutdown();
    }
}
