package io.github.tjheslin1.patterdale;

import io.github.tjheslin1.patterdale.metrics.probe.ProbeDefinition;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static io.github.tjheslin1.patterdale.PatterdaleRuntimeParameters.patterdaleRuntimeParameters;
import static java.util.Collections.singletonList;

public class PatterdaleRuntimeParametersTest implements WithAssertions {

    @Test
    public void extractsRuntimeParametersFromUnmarshalledConfigurationFile() throws Exception {
        PatterdaleRuntimeParameters expectedParameters = new PatterdaleRuntimeParameters(
                HTTP_PORT,
                USER,
                PASSWORD,
                JDBC_URL,
                MAX_SIZE,
                MIN_IDLE,
                PROBES);

        PatterdaleRuntimeParameters actualParameters = patterdaleRuntimeParameters(exampleConfig());
        assertThat(actualParameters).isEqualTo(expectedParameters);
    }

    private static PatterdaleConfig exampleConfig() {
        PatterdaleConfig config = new PatterdaleConfig();

        config.httpPort = HTTP_PORT;

        HashMap<String, String> databaseProperties = new HashMap<>();
        databaseProperties.put("user", USER);
        databaseProperties.put("password", PASSWORD);
        databaseProperties.put("jdbcUrl", JDBC_URL);
        config.database = databaseProperties;

        HashMap<String, String> connectionPoolProperties = new HashMap<>();
        connectionPoolProperties.put("maxSize", Integer.toString(MAX_SIZE));
        connectionPoolProperties.put("minIdle", Integer.toString(MIN_IDLE));
        config.connectionPool = connectionPoolProperties;

        HashMap[] probes = new HashMap[1];
        probes[0] = new HashMap();
        probes[0].put("metricName", METRIC_NAME);
        probes[0].put("metricLabel", METRIC_LABELS);
        probes[0].put("query", QUERY_SQL);
        config.probes = probes;

        return config;
    }


    private static final int HTTP_PORT = 7000;

    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final int MAX_SIZE = 5;
    private static final int MIN_IDLE = 1;
    private static final String METRIC_NAME = "test_metric";
    private static final String METRIC_LABELS = "key=value";
    private static final String QUERY_SQL = "SELECT 1 FROM DUAL";
    private static final List<ProbeDefinition> PROBES = singletonList(new ProbeDefinition(QUERY_SQL, METRIC_NAME, METRIC_LABELS));

}