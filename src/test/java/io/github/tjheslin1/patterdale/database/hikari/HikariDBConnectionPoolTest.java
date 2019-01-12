package io.github.tjheslin1.patterdale.database.hikari;

import io.github.tjheslin1.patterdale.database.DBConnection;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import testutil.WithMockito;

public class HikariDBConnectionPoolTest implements WithAssertions, WithMockito {

    private final HikariDBConnection hikariConnection = mock(HikariDBConnection.class);

    private final HikariDBConnectionPool hikariConnectionPool = new HikariDBConnectionPool(hikariConnection);

    @Test
    public void poolReturnsDataSource() throws Exception {
        DBConnection DBConnectionPool = hikariConnectionPool.pool();

        assertThat(DBConnectionPool).isEqualTo(hikariConnection);
    }
}