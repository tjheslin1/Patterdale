package functional;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.WithAssertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.regex.Pattern;

import static testutil.TestUtil.responseBody;
import static testutil.TestUtil.startPatterdale;

public class PatterdaleTest implements WithAssertions {

    @BeforeClass
    public static void setUp() throws InterruptedException {
        startPatterdale("src/test/resources/patterdale-h2.yml", "src/test/resources/passwords-h2.yml");
    }

    @Test
    public void scrapesOracleDatabaseMetricsOnRequest() throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(new HttpGet("http://localhost:7001/metrics"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        assertThat(responseBody(response)).matches(Pattern.compile(
                "database_up\\{database=\"bobsDatabase\",query=\"SELECT 1 FROM DUAL\"} 1.0\n" +
                        "database_up\\{database=\"alicesDatabase\",query=\"SELECT 1 FROM DUAL\"} 1.0\n" +
                        ".*"
                , Pattern.DOTALL)
        );
    }

    @Test
    public void readyPageReturns200andOK() throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(new HttpGet("http://localhost:7001/ready"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        assertThat(responseBody(response)).contains("OK");
    }
}
