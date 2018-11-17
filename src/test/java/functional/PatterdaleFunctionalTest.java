package functional;

import io.github.tjheslin1.patterdale.Patterdale;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.WithAssertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.regex.Pattern;

import static testutil.TestUtil.responseBody;
import static testutil.TestUtil.startPatterdale;

public class PatterdaleFunctionalTest implements WithAssertions {

    private static Patterdale patterdale;

    @BeforeClass
    public static void setUp() {
        patterdale = startPatterdale("src/test/resources/patterdale-h2.yml", "src/test/resources/passwords-h2.yml");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        patterdale.stop();
    }

    @Test
    public void scrapesOracleDatabaseMetricsOnRequest() throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(new HttpGet("http://localhost:7001/metrics"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        String responseBody = responseBody(response);
        assertThat(responseBody).matches(Pattern.compile(
                "database_up\\{database=\"bobsDatabase\",label=\"value\",query=\"SELECT 1 FROM DUAL\"} 1.0\n" +
                        "database_up\\{database=\"alicesDatabase\",query=\"SELECT 1 FROM DUAL\"} 1.0\n" +
                        ".*"
                , Pattern.DOTALL)
        );

        // the order of the jvm and jetty metrics changes
        assertThat(responseBody).matches(Pattern.compile(".*jvm.*\\{.*}.*", Pattern.DOTALL));
        assertThat(responseBody).matches(Pattern.compile(".*jetty.*\\{.*}.*", Pattern.DOTALL));
    }

    @Test
    public void readyPageReturns200andOK() throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(new HttpGet("http://localhost:7001/ready"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        assertThat(responseBody(response)).contains("OK");
    }
}
