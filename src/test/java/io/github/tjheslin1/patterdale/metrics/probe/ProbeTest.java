package io.github.tjheslin1.patterdale.metrics.probe;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition.databaseDefinition;
import static java.util.Collections.emptyList;

public class ProbeTest implements WithAssertions {

    @Test
    public void removesTrailingSemicolon() {
        Probe probe = Probe.probe("name", "SELECT 1 FROM DUAL ; ", "", "", "");

        assertThat(probe.query()).isEqualTo("SELECT 1 FROM DUAL");
    }

    @Test
    public void dbLabelledPassesMetricLabelsFromDatabaseDefinitionToProbe() {
        final Probe probe = Probe.probe("name", "SELECT 1 FROM DUAL ; ", "", "", "probeLabel=\"probeValue\"");
        final Map<String, String> labels = new HashMap<>();
        labels.put("label1", "value1");
        labels.put("label2", "value2");

        final Probe got = probe.dbLabelled(databaseDefinition("db", "user", "url", emptyList(), labels));

        assertThat(got.metricLabels).isEqualTo("database=\"db\",label1=\"value1\",label2=\"value2\",probeLabel=\"probeValue\"");
    }
}