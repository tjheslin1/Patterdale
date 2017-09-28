package io.github.tjheslin1.patterdale.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition;
import io.github.tjheslin1.patterdale.metrics.probe.Probe;
import org.assertj.core.api.WithAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import testutil.WithMockito;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;

import static io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition.databaseDefinition;
import static io.github.tjheslin1.patterdale.metrics.probe.Probe.probe;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class ConfigUnmarshallerTest implements WithAssertions, WithMockito {

    private final Logger logger = mock(Logger.class);

    private final ConfigUnmarshaller configUnmarshaller = new ConfigUnmarshaller(logger);

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void unmarshallConfigFileToPatterdaleConfig() throws Exception {
        PatterdaleConfig patterdaleConfig = configUnmarshaller.parseConfig(loadTestConfigFile());

        assertThat(patterdaleConfig.httpPort).isEqualTo(expectedConfig().httpPort);
        assertThat(patterdaleConfig.cacheDuration).isEqualTo(expectedConfig().cacheDuration);
        assertThat(patterdaleConfig.databases).isEqualTo(expectedConfig().databases);
        assertThat(patterdaleConfig.probes).isEqualTo(expectedConfig().probes);
        assertThat(patterdaleConfig.connectionPool).isEqualTo(expectedConfig().connectionPool);
    }

    @Test
    public void blowsUpIfPassWordsFileIsMalformed() throws Exception {
        File tempFile = temporaryFolder.newFile("patterdale.yml");
        FileWriter fileWriter = new FileWriter(tempFile);
        fileWriter.write("invalid content");
        fileWriter.flush();

        assertThatThrownBy(() -> configUnmarshaller.parseConfig(tempFile))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage(format("Error occurred reading config file '%s'.", tempFile.getName()))
                .hasCauseExactlyInstanceOf(JsonMappingException.class);
    }

    @SuppressWarnings("ConstantConditions")
    private File loadTestConfigFile() {
        URL url = this.getClass().getClassLoader().getResource("patterdale.yml");
        return new File(url.getPath());
    }

    private PatterdaleConfig expectedConfig() {
        PatterdaleConfig expectedConfig = new PatterdaleConfig();

        expectedConfig.httpPort = 7001;
        expectedConfig.cacheDuration = 60;
        expectedConfig.databases = new DatabaseDefinition[]{
                databaseDefinition(NAME, USER, JDBC_URL, singletonList("healthCheck")),
                databaseDefinition(NAME_2, USER, JDBC_URL_2, asList("healthCheck", "slowestQueries"))
        };

        HashMap<String, String> connectionPoolProperties = new HashMap<>();
        connectionPoolProperties.put("maxSize", "5");
        connectionPoolProperties.put("minIdle", "1");
        expectedConfig.connectionPool = connectionPoolProperties;

        expectedConfig.probes = new Probe[]{
                PROBE_1, PROBE_2
        };

        return expectedConfig;
    }

    private static final String NAME = "bobsDatabase";
    private static final String NAME_2 = "alicesDatabase";
    private static final String USER = "system";
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1522:xe";
    private static final String JDBC_URL_2 = "jdbc:oracle:thin:@localhost:1523:xe";
    private static final String EXISTS = "exists";
    private static final String LIST = "list";
    private static final String METRIC_NAME = "database_up";
    private static final String METRIC_NAME_2 = "slowest_queries";
    private static final String METRIC_LABELS = "query=\"SELECT 1 FROM DUAL\"";
    private static final String METRIC_LABELS_2 = "sqlText=\"%s\",sqlId=\"%s\",username=\"%s\",childNumber=\"%s\",diskReads=\"%s\",executions=\"%s\",firstLoadTime=\"%s\",lastLoadTime=\"%s\"";
    private static final String QUERY_SQL_1 = "SELECT 1 FROM DUAL";
    private static final Probe PROBE_1 = probe("healthCheck", QUERY_SQL_1, EXISTS, METRIC_NAME, METRIC_LABELS);
    private static final String QUERY_SQL_2 = "SELECT * FROM\n" +
            "(SELECT\n" +
            "    s.elapsed_time / s.executions / 1000 AS AVG_ELAPSED_TIME_IN_MILLIS,\n" +
            "    SUBSTR(s.sql_fulltext, 1, 80) AS SQL_TEXT,\n" +
            "    s.sql_id,\n" +
            "    d.username,\n" +
            "    s.child_number,\n" +
            "    s.disk_reads,\n" +
            "    s.executions,\n" +
            "    s.first_load_time,\n" +
            "    s.last_load_time\n" +
            "FROM    v$sql s, dba_users d\n" +
            "WHERE   s.parsing_user_id = d.user_id\n" +
            "AND trunc(TO_DATE(s.last_load_time, 'YYYY-MM-DD/HH24:MI:SS')) >= trunc(SYSDATE - 1)\n" +
            "ORDER BY elapsed_time DESC)\n" +
            "WHERE ROWNUM <= 5;";
    private static final Probe PROBE_2 = probe("slowestQueries", QUERY_SQL_2, LIST, METRIC_NAME_2, METRIC_LABELS_2);
}