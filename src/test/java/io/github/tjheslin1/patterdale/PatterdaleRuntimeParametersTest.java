package io.github.tjheslin1.patterdale;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.HashMap;

import static io.github.tjheslin1.patterdale.PatterdaleRuntimeParameters.patterdaleRuntimeParameters;

public class PatterdaleRuntimeParametersTest implements WithAssertions {

    @Test
    public void extractsRuntimeParametersFromUnmarshalledConfigurationFile() throws Exception {
        PatterdaleRuntimeParameters expectedParameters = new PatterdaleRuntimeParameters(
                HTTP_PORT,
                USER,
                PASSWORD,
                JDBC_URL,
                MAX_SIZE,
                MIN_IDLE
        );

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
        return config;
    }

    private static final int HTTP_PORT = 7000;
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final int MAX_SIZE = 5;
    private static final int MIN_IDLE = 1;
}