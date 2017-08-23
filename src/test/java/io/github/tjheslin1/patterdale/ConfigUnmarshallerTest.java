package io.github.tjheslin1.patterdale;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.slf4j.Logger;
import testutil.WithMockito;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

public class ConfigUnmarshallerTest implements WithAssertions, WithMockito {

    private final Logger logger = mock(Logger.class);

    private final ConfigUnmarshaller configUnmarshaller = new ConfigUnmarshaller(logger);

    @Test
    public void unmarshallConfigFileToRuntimeParameters() throws Exception {
        File file = loadTestConfigFile();
        PatterdaleConfig patterdaleConfig = configUnmarshaller.parseConfig(file);

        assertThat(patterdaleConfig.httpPort).isEqualTo(expectedConfig().httpPort);
        assertThat(patterdaleConfig.database).isEqualTo(expectedConfig().database);
        assertThat(patterdaleConfig.connectionPool).isEqualTo(expectedConfig().connectionPool);
        assertThat(patterdaleConfig.probes).isEqualTo(expectedConfig().probes);
    }

    private File loadTestConfigFile() {
        URL url = this.getClass().getClassLoader().getResource("patterdale.yml");
        return new File(url.getPath());
    }

    private PatterdaleConfig expectedConfig() {
        PatterdaleConfig expectedConfig = new PatterdaleConfig();

        expectedConfig.httpPort = 7000;

        HashMap<String, String> databaseProperties = new HashMap<>();
        databaseProperties.put("user", "system");
        databaseProperties.put("password", "oracle");
        databaseProperties.put("jdbcUrl", "jdbc:oracle:thin:system/oracle@localhost:1521:xe");
        expectedConfig.database = databaseProperties;

        HashMap<String, String> connectionPoolProperties = new HashMap<>();
        connectionPoolProperties.put("maxSize", "5");
        connectionPoolProperties.put("minIdle", "1");
        expectedConfig.connectionPool = connectionPoolProperties;

        HashMap[] probes = new HashMap[1];
        probes[0] = new HashMap<>();
        probes[0].put("metricName", "database_up");
        probes[0].put("metricLabel", "database=myDB");
        probes[0].put("query", "SELECT 1 FROM DUAL");
        expectedConfig.probes = probes;

        return expectedConfig;
    }
}