package resiliency;

import io.github.tjheslin1.patterdale.Patterdale;
import io.github.tjheslin1.patterdale.database.hikari.OracleDataSourceProvider;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.regex.Pattern;

import static testutil.TestUtil.responseBody;
import static testutil.TestUtil.startPatterdale;

public class StartupResiliencyTest implements WithAssertions {

    @Test(timeout = 30000)
    public void startUpCompletesEvenIfDatabaseIsDown() throws Exception {
        // cannot use @Before and @After as startup of app needs to be included in timeout
        Patterdale patterdale = startPatterdale(new OracleDataSourceProvider(), "src/test/resources/patterdale.yml", "src/test/resources/passwords.yml");

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(new HttpGet("http://localhost:7001/metrics"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        String responseBody = responseBody(response);

        patterdale.stop();

        assertThat(responseBody).doesNotMatch(Pattern.compile(
                "database_up\\{database=\"bobsDatabase.*\n" +
                        "no_labels\\{database=\"bobsDatabase.*\n" +
                        "database_up\\{database=\"alicesDatabase.*\n" +
                        "slowest_queries.*\n" +
                        ".*"
                , Pattern.DOTALL)
        );

        // the order of the jvm and jetty metrics changes
        assertThat(responseBody).matches(Pattern.compile(".*jvm.*\\{.*}.*", Pattern.DOTALL));
        assertThat(responseBody).matches(Pattern.compile(".*jetty.*\\{.*}.*", Pattern.DOTALL));
    }
}
