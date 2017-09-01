package io.github.tjheslin1.patterdale.metrics.probe;

import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.slf4j.Logger;

import static org.mockito.Mockito.mock;

public class TypeToProbeMapperTest implements WithAssertions {

    private final DBConnectionPool dbConnectionPool = mock(DBConnectionPool.class);
    private final Logger logger = mock(Logger.class);

    @Test
    public void mapsKnownTypeToSqlProbeClass() throws Exception {
        OracleSQLProbe oracleSQLProbe = new TypeToProbeMapper().createProbe(
                new ProbeDefinition("SQL", "exists", "metricName", "metricLabel"),
                dbConnectionPool, logger);

        assertThat(oracleSQLProbe).isExactlyInstanceOf(ExistsOracleSQLProbe.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void blowsUpForUnknownTypeParameter() throws Exception {
        new TypeToProbeMapper().createProbe(
                new ProbeDefinition("SQL", "blah", "metricName", "metricLabel"),
                dbConnectionPool, logger);
    }

    @Test
    public void providedTypeIsCaseInsensitive() throws Exception {
        OracleSQLProbe oracleSQLProbe = new TypeToProbeMapper().createProbe(
                new ProbeDefinition("SQL", "eXiStS", "metricName", "metricLabel"),
                dbConnectionPool, logger);

        assertThat(oracleSQLProbe).isExactlyInstanceOf(ExistsOracleSQLProbe.class);
    }
}
