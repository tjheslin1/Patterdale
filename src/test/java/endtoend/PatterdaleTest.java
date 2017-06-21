package endtoend;

import io.github.tjheslin1.patterdale.ConfigUnmarshaller;
import io.github.tjheslin1.patterdale.Patterdale;
import io.github.tjheslin1.patterdale.PatterdaleConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static io.github.tjheslin1.patterdale.PatterdaleRuntimeParameters.patterdaleRuntimeParameters;

public class PatterdaleTest implements WithAssertions {

    private Logger logger;

    @Before
    public void setUp() {
        System.setProperty("config.file", "src/test/resources/patterdale.yml");
        logger = LoggerFactory.getLogger("io.github.tjheslin1.patterdale.Patterdale");
    }

    @Test
    public void scrapesOracleDatabaseMetricsOnRequest() throws Exception {
        PatterdaleConfig patterdaleConfig = new ConfigUnmarshaller(logger)
                .parseConfig(new File(System.getProperty("config.file")));

        new Patterdale(patterdaleRuntimeParameters(patterdaleConfig), logger)
                .start();

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(new HttpGet("http://localhost:7000/metrics"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        assertThat(responseBody(response)).isEqualTo("oracle_health_check 1");
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
