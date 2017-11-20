package endtoend;

import io.github.tjheslin1.patterdale.Patterdale;
import io.github.tjheslin1.patterdale.config.PatterdaleRuntimeParameters;
import io.github.tjheslin1.patterdale.config.ConfigUnmarshaller;
import io.github.tjheslin1.patterdale.config.Passwords;
import io.github.tjheslin1.patterdale.config.PasswordsUnmarshaller;
import io.github.tjheslin1.patterdale.config.PatterdaleConfig;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import io.github.tjheslin1.patterdale.database.hikari.HikariDBConnection;
import io.github.tjheslin1.patterdale.database.hikari.HikariDBConnectionPool;
import io.github.tjheslin1.patterdale.metrics.probe.Probe;
import io.github.tjheslin1.patterdale.metrics.probe.TypeToProbeMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.WithAssertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.github.tjheslin1.patterdale.config.PatterdaleRuntimeParameters.patterdaleRuntimeParameters;
import static io.github.tjheslin1.patterdale.database.hikari.HikariDataSourceProvider.retriableDataSource;
import static java.util.stream.Collectors.toMap;

public class PatterdaleTest implements WithAssertions {

    private static TypeToProbeMapper typeToProbeMapper;
    private static Logger logger;
    private static PatterdaleRuntimeParameters runtimeParameters;
    private static Map<String, DBConnectionPool> connectionPools;
    private static Map<String, Probe> probesByName;

    @BeforeClass
    public static void setUp() {
        logger = LoggerFactory.getLogger("application");

        PatterdaleConfig patterdaleConfig = new ConfigUnmarshaller(logger)
                .parseConfig(new File("src/test/resources/patterdale.yml"));

        Passwords passwords = new PasswordsUnmarshaller(logger)
                .parsePasswords(new File("src/test/resources/passwords.yml"));

        runtimeParameters = patterdaleRuntimeParameters(patterdaleConfig);

        connectionPools = runtimeParameters.databases().stream()
                .collect(Collectors.toMap(databaseDefinition -> databaseDefinition.name,
                        databaseDefinition -> new HikariDBConnectionPool(new HikariDBConnection(retriableDataSource(runtimeParameters, databaseDefinition, passwords, logger)))));

        probesByName = runtimeParameters.probes().stream()
                .collect(toMap(probe -> probe.name, probe -> probe));

        typeToProbeMapper = new TypeToProbeMapper(logger);

        new Patterdale(runtimeParameters, connectionPools, typeToProbeMapper, probesByName, logger)
                .start();
    }

    @Test
    public void scrapesOracleDatabaseMetricsOnRequest() throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(new HttpGet("http://localhost:7001/metrics"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        assertThat(responseBody(response)).matches(Pattern.compile(
                "database_up\\{database=\"bobsDatabase\",query=\"SELECT 1 FROM DUAL\"} 1.0\n" +
                        "database_up\\{database=\"alicesDatabase\",query=\"SELECT 1 FROM DUAL\"} 1.0\n" +
                        "slowest_queries\\{database=\"alicesDatabase\",sqlText=.*,sqlId=.*,username=.*,childNumber=.*,diskReads=.*,executions=.*,firstLoadTime=.*,lastLoadTime=.*} .*\n" +
                        "slowest_queries\\{database=\"alicesDatabase\",sqlText=.*,sqlId=.*,username=.*,childNumber=.*,diskReads=.*,executions=.*,firstLoadTime=.*,lastLoadTime=.*} .*\n" +
                        "slowest_queries\\{database=\"alicesDatabase\",sqlText=.*,sqlId=.*,username=.*,childNumber=.*,diskReads=.*,executions=.*,firstLoadTime=.*,lastLoadTime=.*} .*\n" +
                        "slowest_queries\\{database=\"alicesDatabase\",sqlText=.*,sqlId=.*,username=.*,childNumber=.*,diskReads=.*,executions=.*,firstLoadTime=.*,lastLoadTime=.*} .*\n" +
                        "slowest_queries\\{database=\"alicesDatabase\",sqlText=.*,sqlId=.*,username=.*,childNumber=.*,diskReads=.*,executions=.*,firstLoadTime=.*,lastLoadTime=.*} .*"
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

    private String responseBody(HttpResponse response) throws IOException {
        try (Scanner scanner = new Scanner(response.getEntity().getContent(), StandardCharsets.UTF_8.toString()).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
