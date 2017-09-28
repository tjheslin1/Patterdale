package io.github.tjheslin1.patterdale.metrics.probe;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

public class ProbeTest implements WithAssertions {

    @Test
    public void removesTrailingSemicolon() {
        Probe probe = Probe.probe("name", "SELECT 1 FROM DUAL ; ", "", "", "");

        assertThat(probe.query()).isEqualTo("SELECT 1 FROM DUAL");
    }
}