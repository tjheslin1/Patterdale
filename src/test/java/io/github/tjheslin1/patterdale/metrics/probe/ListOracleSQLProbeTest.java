package io.github.tjheslin1.patterdale.metrics.probe;

import io.github.tjheslin1.patterdale.config.RuntimeParameters;
import io.github.tjheslin1.patterdale.database.DBConnection;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.slf4j.Logger;
import testutil.WithMockito;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.concurrent.Future;

import static io.github.tjheslin1.patterdale.metrics.probe.Probe.probe;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ListOracleSQLProbeTest implements WithAssertions, WithMockito {

    private static final Probe PROBE = probe("name", "SQL", "exists", "name", "label");

    private final ResultSet resultSet = mock(ResultSet.class);
    private final ResultSetMetaData resultSetMetaData = mock(ResultSetMetaData.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final Connection connection = mock(Connection.class);
    private final DBConnection dbConnection = mock(DBConnection.class);
    private final DBConnectionPool dbConnectionPool = mock(DBConnectionPool.class);
    private final Future<DBConnectionPool> futureConnectionPool = mock(Future.class);
    private final RuntimeParameters runtimeParameters = mock(RuntimeParameters.class);
    private final Logger logger = mock(Logger.class);

    private final ListOracleSQLProbe listOracleSQLProbe = new ListOracleSQLProbe(PROBE, futureConnectionPool, runtimeParameters, logger);

    @Test
    public void probeReturnsMultipleSuccess() throws Exception {
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSetMetaData.getColumnCount()).thenReturn(2);
        when(resultSet.getDouble(1)).thenReturn(4.5, 6.7);
        when(resultSet.getString(2)).thenReturn("example SQL", "example SQL2");
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(dbConnection.connection()).thenReturn(connection);
        when(futureConnectionPool.get(anyLong(), eq(SECONDS))).thenReturn(dbConnectionPool);
        when(dbConnectionPool.pool()).thenReturn(dbConnection);
        when(runtimeParameters.probeConnectionWaitInSeconds()).thenReturn(10);

        List<ProbeResult> probeResults = listOracleSQLProbe.probes();

        assertThat(probeResults).containsExactly(
                new ProbeResult(4.5, PROBE, singletonList("example SQL")),
                new ProbeResult(6.7, PROBE, singletonList("example SQL2"))
        );
    }

    @Test
    public void probeFailsAndReturnsEmptyList() throws Exception {
        when(preparedStatement.executeQuery()).thenThrow(IOException.class);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(dbConnection.connection()).thenReturn(connection);
        when(dbConnectionPool.pool()).thenReturn(dbConnection);

        List<ProbeResult> probeResults = listOracleSQLProbe.probes();

        assertThat(probeResults).isEmpty();
    }

    @Test
    public void probeAssumesFirstColumnIsProbeValueAndRemainingColumnsAreDynamicLabels() throws Exception {
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSetMetaData.getColumnCount()).thenReturn(3);
        when(resultSet.getDouble(1)).thenReturn(4.5);
        when(resultSet.getString(2)).thenReturn("example SQL");
        when(resultSet.getString(3)).thenReturn("dynamicLabel3");
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(dbConnection.connection()).thenReturn(connection);
        when(futureConnectionPool.get(anyLong(), eq(SECONDS))).thenReturn(dbConnectionPool);
        when(dbConnectionPool.pool()).thenReturn(dbConnection);
        when(runtimeParameters.probeConnectionWaitInSeconds()).thenReturn(10);

        List<ProbeResult> probeResults = listOracleSQLProbe.probes();

        assertThat(probeResults).containsExactly(
                new ProbeResult(4.5, PROBE, asList("example SQL", "dynamicLabel3"))
        );
    }
}