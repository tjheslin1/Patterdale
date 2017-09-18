package io.github.tjheslin1.patterdale;

import io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition;
import io.github.tjheslin1.patterdale.metrics.probe.Probe;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.slf4j.Logger;
import testutil.WithMockito;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition.databaseDefinition;
import static io.github.tjheslin1.patterdale.metrics.probe.Probe.probe;
import static java.util.Collections.singletonList;

public class ConfigUnmarshallerTest implements WithAssertions, WithMockito {

    private final Logger logger = mock(Logger.class);

    private final ConfigUnmarshaller configUnmarshaller = new ConfigUnmarshaller(logger);

    @Test
    public void unmarshallConfigFileToRuntimeParameters() throws Exception {
        File file = loadTestConfigFile();
        PatterdaleConfig patterdaleConfig = configUnmarshaller.parseConfig(file);

        assertThat(patterdaleConfig.httpPort).isEqualTo(expectedConfig().httpPort);
        assertThat(patterdaleConfig.databases).isEqualTo(expectedConfig().databases);
        assertThat(patterdaleConfig.connectionPool).isEqualTo(expectedConfig().connectionPool);
    }

    private File loadTestConfigFile() {
        URL url = this.getClass().getClassLoader().getResource("patterdale.yml");
        return new File(url.getPath());
    }

    private PatterdaleConfig expectedConfig() {
        PatterdaleConfig expectedConfig = new PatterdaleConfig();

        expectedConfig.httpPort = 7000;
        expectedConfig.databases = new DatabaseDefinition[]{databaseDefinition(USER, PASSWORD, JDBC_URL, PROBES)};

        HashMap<String, String> connectionPoolProperties = new HashMap<>();
        connectionPoolProperties.put("maxSize", "5");
        connectionPoolProperties.put("minIdle", "1");
        expectedConfig.connectionPool = connectionPoolProperties;

        return expectedConfig;
    }

    private static final String USER = "system";
    private static final String PASSWORD = "oracle";
    private static final String JDBC_URL = "jdbc:oracle:thin:system/oracle@localhost:1521:xe";
    private static final String TYPE = "exists";
    private static final String METRIC_NAME = "database_up";
    private static final String METRIC_LABELS = "database=\"myDB\"";
    private static final String QUERY_SQL = "SELECT 1 FROM DUAL";
    private static final List<Probe> PROBES = singletonList(probe(QUERY_SQL, TYPE, METRIC_NAME, METRIC_LABELS));
}