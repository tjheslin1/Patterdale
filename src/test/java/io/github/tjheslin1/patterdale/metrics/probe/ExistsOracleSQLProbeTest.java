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

public class ExistsOracleSQLProbeTest implements WithAssertions, WithMockito {

    private static final String SQL = "SQL";
    private static final String SUCCESS_MESSAGE = "Successful health check.";

    private final ResultSet resultSet = mock(ResultSet.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final Connection connection = mock(Connection.class);
    private final DBConnection dbConnection = mock(DBConnection.class);
    private final DBConnectionPool dbConnectionPool = mock(DBConnectionPool.class);
    private final Logger logger = mock(Logger.class);

    private final ExistsOracleSQLProbe existsOracleSQLProbe = new ExistsOracleSQLProbe(
            new ProbeDefinition(SQL, "",""), dbConnectionPool, logger);

    @Test
    public void probeReturnsSuccess() throws Exception {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(dbConnection.connection()).thenReturn(connection);
        when(dbConnectionPool.pool()).thenReturn(dbConnection);

        ProbeResult probeResult = existsOracleSQLProbe.probe();

        assertThat(probeResult.result).isTrue();
        assertThat(probeResult.message).isEqualTo(SUCCESS_MESSAGE);
    }

    @Test
    public void probeReturnsFailure() throws Exception {
        when(dbConnectionPool.pool()).thenThrow(Exception.class);

        ProbeResult probeResult = existsOracleSQLProbe.probe();

        assertThat(probeResult.result).isFalse();
        assertThat(probeResult.message).isEqualTo("Error occurred executing sql: 'SQL'");
    }
}