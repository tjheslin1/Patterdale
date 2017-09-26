package io.github.tjheslin1.patterdale.metrics.probe;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.github.tjheslin1.patterdale.metrics.probe.Probe.probe;
import static java.util.Arrays.asList;

public class ProbeResultFormatterTest implements WithAssertions {

    private static final Probe PROBE_1 = probe("SQL", "type", "metricName", "metricLabels=\"1\"");
    private static final Probe PROBE_2 = probe("SQL2", "type2", "metricName2", "metricLabels=\"1\",key=\"%s\",label=\"%s\"");

    private static final ProbeResult PROBE_RESULT_1 = new ProbeResult(1.0, PROBE_1);
    private static final ProbeResult PROBE_RESULT_2 = new ProbeResult(4.5, PROBE_2, asList("example SQL", "example label"));

    @Test
    public void formatsProbeResults() throws Exception {
        List<String> formattedProbeResults = ProbeResultFormatter.formatProbeResults(asList(PROBE_RESULT_1, PROBE_RESULT_2));

        assertThat(formattedProbeResults).containsExactly(
                "metricName{metricLabels=\"1\"} 1.0",
                "metricName2{metricLabels=\"1\",key=\"example SQL\",label=\"example label\"} 4.5"
        );
    }
}