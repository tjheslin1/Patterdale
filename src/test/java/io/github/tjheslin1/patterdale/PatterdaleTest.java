package io.github.tjheslin1.patterdale;

import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import io.github.tjheslin1.patterdale.database.HikariDataSourceProvider;
import io.github.tjheslin1.patterdale.metrics.probe.Probe;
import io.github.tjheslin1.patterdale.metrics.probe.TypeToProbeMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import static io.github.tjheslin1.patterdale.metrics.probe.Probe.probe;
import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.mock;
import static testutil.TestUtil.testRuntimeParams;

public class PatterdaleTest implements WithAssertions {

    @Test
    public void lookupProbeShouldThrowRuntimeExceptionForNonExistingKey() {
        final HikariDataSourceProvider provider = mock(HikariDataSourceProvider.class);
        final Logger logger = mock(Logger.class);

        HashMap<String, Future<DBConnectionPool>> connectionPools = new HashMap<>();
        final Map<String, Probe> stringProbeMap = singletonMap("testProbe",
                probe("testProbe", "query", "exists", "name", "value"));

        assertThatThrownBy(() -> {
            new Patterdale(provider, testRuntimeParams(), connectionPools, new TypeToProbeMapper(logger), stringProbeMap, logger)
                    .lookupProbe("unknownKey");
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Probe 'unknownKey' not defined");
    }
}
