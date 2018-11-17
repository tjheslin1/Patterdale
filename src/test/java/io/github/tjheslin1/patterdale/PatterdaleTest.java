package io.github.tjheslin1.patterdale;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.slf4j.Logger;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import io.github.tjheslin1.patterdale.metrics.probe.Probe;
import io.github.tjheslin1.patterdale.metrics.probe.TypeToProbeMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.mock;
import static testutil.TestUtil.testRuntimeParams;
import static io.github.tjheslin1.patterdale.metrics.probe.Probe.probe;

public class PatterdaleTest implements WithAssertions {

    @Test
    public void lookupProbeShouldThrowRuntimeExceptionForNonExistingKey() {
        final Logger logger = mock(Logger.class);

        HashMap<String, Future<DBConnectionPool>> connectionPools = new HashMap<>();
        final Map<String, Probe> stringProbeMap = singletonMap("testProbe",
                probe("testProbe", "query", "exists", "name", "value"));

        assertThatThrownBy(() -> {
            new Patterdale(testRuntimeParams(), connectionPools, new TypeToProbeMapper(logger), stringProbeMap, logger)
                    .lookupProbe("unknownKey");
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Probe 'unknownKey' not defined");
    }
}
