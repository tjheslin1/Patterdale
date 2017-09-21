package io.github.tjheslin1.patterdale.metrics.probe;

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
import java.util.List;

import static io.github.tjheslin1.patterdale.metrics.probe.Probe.probe;

public class ListOracleSQLProbeTest implements WithAssertions, WithMockito {

    private static final Probe PROBE = probe("SQL", "exists", "name", "label");

    private final ResultSet resultSet = mock(ResultSet.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final Connection connection = mock(Connection.class);
    private final DBConnection dbConnection = mock(DBConnection.class);
    private final DBConnectionPool dbConnectionPool = mock(DBConnectionPool.class);
    private final Logger logger = mock(Logger.class);

    private final ListOracleSQLProbe listOracleSQLProbe = new ListOracleSQLProbe(PROBE, dbConnectionPool, logger);

    @Test
    public void probeReturnsMultipleSuccess() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString(1)).thenReturn("example SQL", "example SQL2");
        when(resultSet.getDouble(2)).thenReturn(4.5, 6.7);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(dbConnection.connection()).thenReturn(connection);
        when(dbConnectionPool.pool()).thenReturn(dbConnection);

        List<ProbeResult> probeResults = listOracleSQLProbe.probe();

        assertThat(probeResults).hasSize(2);
        assertThat(probeResults).containsExactly(
                new ProbeResult(4.5, PROBE, "example SQL"),
                new ProbeResult(6.7, PROBE, "example SQL2")
        );
    }

    @Test
    public void probeReturnsSuccessAndFailure() throws Exception {
        when(preparedStatement.executeQuery()).thenThrow(IOException.class);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(dbConnection.connection()).thenReturn(connection);
        when(dbConnectionPool.pool()).thenReturn(dbConnection);

        List<ProbeResult> probeResults = listOracleSQLProbe.probe();

        assertThat(probeResults).isEmpty();
    }
}