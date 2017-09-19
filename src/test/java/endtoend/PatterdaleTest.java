package endtoend;

import io.github.tjheslin1.patterdale.ConfigUnmarshaller;
import io.github.tjheslin1.patterdale.Patterdale;
import io.github.tjheslin1.patterdale.PatterdaleConfig;
import io.github.tjheslin1.patterdale.PatterdaleRuntimeParameters;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import io.github.tjheslin1.patterdale.database.hikari.HikariDBConnection;
import io.github.tjheslin1.patterdale.database.hikari.HikariDBConnectionPool;
import io.github.tjheslin1.patterdale.metrics.probe.TypeToProbeMapper;
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
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.tjheslin1.patterdale.Patterdale.dataSource;
import static io.github.tjheslin1.patterdale.PatterdaleRuntimeParameters.patterdaleRuntimeParameters;

public class PatterdaleTest implements WithAssertions {

    private TypeToProbeMapper typeToProbeMapper;
    private Logger logger;
    private PatterdaleRuntimeParameters runtimeParameters;
    private Map<String, DBConnectionPool> connectionPools;

    @Before
    public void setUp() {
        System.setProperty("config.file", "src/test/resources/patterdale.yml");
        logger = LoggerFactory.getLogger("io.github.tjheslin1.patterdale.Patterdale");

        PatterdaleConfig patterdaleConfig = new ConfigUnmarshaller(logger)
                .parseConfig(new File(System.getProperty("config.file")));

        runtimeParameters = patterdaleRuntimeParameters(patterdaleConfig);

        connectionPools = runtimeParameters.databases().stream()
                .collect(Collectors.toMap(databaseDefinition -> databaseDefinition.name,
                        databaseDefinition -> new HikariDBConnectionPool(new HikariDBConnection(dataSource(runtimeParameters, databaseDefinition, logger)))));

        typeToProbeMapper = new TypeToProbeMapper(logger);
    }

    @Test
    public void scrapesOracleDatabaseMetricsOnRequest() throws Exception {
        new Patterdale(runtimeParameters, connectionPools, typeToProbeMapper, logger)
                .start();

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(new HttpGet("http://localhost:7001/metrics"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        assertThat(responseBody(response)).isEqualTo(
                "database_up{database=\"myDB\",query=\"SELECT 1 FROM DUAL\"} 1" +
                        "database_up{database=\"myDB2\",query=\"SELECT 1 FROM DUAL\"} 1" +
                        "database_up{database=\"myDB2\",query=\"SELECT 2 FROM DUAL\"} 0"
        );
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
