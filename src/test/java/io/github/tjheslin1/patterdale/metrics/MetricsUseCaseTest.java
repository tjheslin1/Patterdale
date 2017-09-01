package io.github.tjheslin1.patterdale.metrics;

import io.github.tjheslin1.patterdale.database.DBConnection;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import io.github.tjheslin1.patterdale.metrics.probe.ExistsOracleSQLProbe;
import io.github.tjheslin1.patterdale.metrics.probe.OracleSQLProbe;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeDefinition;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;
import org.assertj.core.api.WithAssertions;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import testutil.WithMockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class MetricsUseCaseTest implements WithAssertions, WithMockito {

    private static final ProbeDefinition PROBE_DEFINITION = new ProbeDefinition("SQL", "exists", "name", "label");

    private final ResultSet resultSet = mock(ResultSet.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final Connection connection = mock(Connection.class);
    private final DBConnection dbConnection = mock(DBConnection.class);
    private final DBConnectionPool dbConnectionPool = mock(DBConnectionPool.class);
    private final Logger logger = mock(Logger.class);

    @Test
    public void scrapeMetricsReturnsSuccess() throws Exception {
        givenAllProbesAreSuccessful();

        List<OracleSQLProbe> probes = singletonList(new ExistsOracleSQLProbe(PROBE_DEFINITION, dbConnectionPool, logger));

        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
        List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();

        assertThat(probeResults)
                .isEqualTo(singletonList(new ProbeResult(true, "Successful health check.", PROBE_DEFINITION)));
    }

    @Test
    public void scrapeMetricsReturnsSuccessForMultipleProbes() throws Exception {
        givenAllProbesAreSuccessful();
        List<OracleSQLProbe> probes = asList(
                new ExistsOracleSQLProbe(PROBE_DEFINITION, dbConnectionPool, logger),
                new ExistsOracleSQLProbe(PROBE_DEFINITION, dbConnectionPool, logger)
        );

        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
        List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();

        assertThat(probeResults).isEqualTo(asList(
                new ProbeResult(true, "Successful health check.", PROBE_DEFINITION),
                new ProbeResult(true, "Successful health check.", PROBE_DEFINITION)
        ));
    }

    @Test
    public void scrapeMetricsReturnsFailureIfAnyProbeFails() throws Exception {
        givenASuccessfulProbeFollowedByAFailedProbe();
        List<OracleSQLProbe> probes = asList(
                new ExistsOracleSQLProbe(PROBE_DEFINITION, dbConnectionPool, logger),
                new ExistsOracleSQLProbe(PROBE_DEFINITION, dbConnectionPool, logger)
        );

        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
        List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();

        assertThat(probeResults).isEqualTo(asList(
                new ProbeResult(true, "Successful health check.", PROBE_DEFINITION),
                new ProbeResult(false, "Expected a result of '1' from SQL query 'SQL' but got '0'", PROBE_DEFINITION)
        ));
    }

    @Test
    public void scrapeMetricsReturnsFailureIfAllProbeFails() throws Exception {
        givenAllFailedProbes();
        List<OracleSQLProbe> probes = asList(
                new ExistsOracleSQLProbe(PROBE_DEFINITION, dbConnectionPool, logger),
                new ExistsOracleSQLProbe(PROBE_DEFINITION, dbConnectionPool, logger)
        );

        MetricsUseCase metricsUseCase = new MetricsUseCase(probes);
        List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();

        assertThat(probeResults).isEqualTo(asList(
                new ProbeResult(false, "Expected a result of '1' from SQL query 'SQL' but got '0'", PROBE_DEFINITION),
                new ProbeResult(false, "Expected a result of '1' from SQL query 'SQL' but got '0'", PROBE_DEFINITION)
        ));
    }

    private void givenAllProbesAreSuccessful() throws SQLException {
        givenProbesReturnValues(1);
    }

    private void givenASuccessfulProbeFollowedByAFailedProbe() throws SQLException {
        givenProbesReturnValues(1, 0);
    }

    private void givenAllFailedProbes() throws SQLException {
        givenProbesReturnValues(0);
    }

    private void givenProbesReturnValues(int firstValue, Integer... subsequenceValues) throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(firstValue, subsequenceValues);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(dbConnection.connection()).thenReturn(connection);
        when(dbConnectionPool.pool()).thenReturn(dbConnection);
    }
}