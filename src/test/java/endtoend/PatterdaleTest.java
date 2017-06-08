package endtoend;

import io.github.tjheslin1.patterdale.Patterdale;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PatterdaleTest implements WithAssertions {

    @Test
    public void scrapesOracleDatabaseMetricsOnRequest() throws Exception {
        Patterdale.start();

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(new HttpGet("http://localhost:7000/metrics"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        assertThat(responseBody(response)).isEqualTo("success");
    }

    private String responseBody(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
