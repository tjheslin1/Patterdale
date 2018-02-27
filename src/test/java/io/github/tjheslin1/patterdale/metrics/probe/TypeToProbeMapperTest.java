package io.github.tjheslin1.patterdale.metrics.probe;

import io.github.tjheslin1.patterdale.config.RuntimeParameters;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.slf4j.Logger;
import testutil.WithMockito;

import java.util.concurrent.Future;

import static io.github.tjheslin1.patterdale.metrics.probe.Probe.probe;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class TypeToProbeMapperTest implements WithAssertions, WithMockito {

    private static final Probe EXIST_PROBE_DEFINITION = probe("name", "SQL", "exists", "metricName", "metricLabels");
    private static final DatabaseDefinition DATABASE_DEFINITION = DatabaseDefinition.databaseDefinition("dbName", "user", "url", emptyList(), emptyMap());

    private final Future<DBConnectionPool> dbConnectionPool = mock(Future.class);
    private final RuntimeParameters runtimeParameters = mock(RuntimeParameters.class);
    private final Logger logger = mock(Logger.class);

    @Test
    public void mapsKnownTypeToSqlProbeClass() throws Exception {
        OracleSQLProbe oracleSQLProbe = new TypeToProbeMapper(logger).createProbe(DATABASE_DEFINITION, dbConnectionPool, EXIST_PROBE_DEFINITION, runtimeParameters);

        assertThat(oracleSQLProbe).isExactlyInstanceOf(ExistsOracleSQLProbe.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void blowsUpForUnknownTypeParameter() throws Exception {
        new TypeToProbeMapper(logger).createProbe(DATABASE_DEFINITION, dbConnectionPool, probe("name", "", "none", "", ""), runtimeParameters);
    }
}