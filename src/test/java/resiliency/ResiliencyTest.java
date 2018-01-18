package resiliency;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.WithAssertions;
import org.junit.Ignore;
import org.junit.Test;

import static testutil.TestUtil.responseBody;
import static testutil.TestUtil.startPatterdale;

public class ResiliencyTest implements WithAssertions {

    @Ignore
    @Test(timeout = 2000)
    public void startUpCompletesEvenIfDatabaseIsDown() throws Exception {
        startPatterdale("src/test/resources/patterdale.yml", "src/test/resources/passwords.yml");

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(new HttpGet("http://localhost:7001/metrics"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        assertThat(responseBody(response)).isEqualTo("");
    }
}
