package io.github.tjheslin1.patterdale.metrics.probe;

import io.github.tjheslin1.patterdale.database.DBConnection;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.slf4j.Logger;
import testutil.WithMockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static io.github.tjheslin1.patterdale.metrics.probe.Probe.probe;

public class ExistsOracleSQLProbeTest implements WithAssertions, WithMockito {

    private static final String SQL = "SQL";

    private final ResultSet resultSet = mock(ResultSet.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final Connection connection = mock(Connection.class);
    private final DBConnection dbConnection = mock(DBConnection.class);
    private final DBConnectionPool dbConnectionPool = mock(DBConnectionPool.class);
    private final Logger logger = mock(Logger.class);

    private final ExistsOracleSQLProbe existsOracleSQLProbe = new ExistsOracleSQLProbe(
            probe("name", SQL, "exists", "", ""), dbConnectionPool, logger);

    @Test
    public void probeReturnsSuccess() throws Exception {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(dbConnection.connection()).thenReturn(connection);
        when(dbConnectionPool.pool()).thenReturn(dbConnection);

        ProbeResult probeResult = existsOracleSQLProbe.probe().get(0);

        assertThat(probeResult.value).isEqualTo(1);
        assertThat(probeResult.dynamicLabelValues).isEmpty();
    }

    @Test
    public void probeReturnsFailure() throws Exception {
        when(dbConnectionPool.pool()).thenThrow(Exception.class);

        ProbeResult probeResult = existsOracleSQLProbe.probe().get(0);

        assertThat(probeResult.value).isEqualTo(0);
        assertThat(probeResult.dynamicLabelValues).isEmpty();
    }
}