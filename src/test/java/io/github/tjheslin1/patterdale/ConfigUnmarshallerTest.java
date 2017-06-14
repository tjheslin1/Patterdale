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
        URL url = this.getClass().getClassLoader().getResource("local.yml");
        File file = new File(url.getPath());
        PatterdaleConfig patterdaleConfig = configUnmarshaller.parseConfig(file);

        assertThat(patterdaleConfig).isEqualTo(expectedConfig());
    }

    private PatterdaleConfig expectedConfig() {
        PatterdaleConfig expectedConfig = new PatterdaleConfig();

        expectedConfig.httpPort = 7000;
        expectedConfig.logbackConfiguration = "src/main/resources/logback.xml";

        HashMap<String, String> databaseProperties = new HashMap<>();
        databaseProperties.put("serverName", "primary");
        databaseProperties.put("name", "dual");
        databaseProperties.put("networkProtocol", "tcp");
        databaseProperties.put("driverType", "thin");
        databaseProperties.put("jdbcUrl", "jdbc:oracle:thin:system/oracle@localhost:1521:xe");
        expectedConfig.database = databaseProperties;

        HashMap<String, String> connectionPoolProperties = new HashMap<>();
        connectionPoolProperties.put("maxSize", "5");
        connectionPoolProperties.put("minIdle", "1");
        expectedConfig.connectionPool = connectionPoolProperties;
        return expectedConfig;
    }
}