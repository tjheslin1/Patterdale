/*
 * Copyright 2017 - 2021 Thomas Heslin <tjheslin1@kolabnow.com>.
 *
 * This file is part of Patterdale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tjheslin1.patterdale.metrics.probe;

import java.io.IOException;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class ProbeResultFormatter {

    public static List<String> formatProbeResults(List<ProbeResult> probeResults) throws IOException {
        return probeResults.stream()
                .map(ProbeResultFormatter::formatProbe)
                .collect(toList());
    }

    private static String formatProbe(ProbeResult probeResult) {
        String metricLabels = appendDynamicLabel(probeResult);

        return format("%s{%s} %s",
                probeResult.probe.metricName,
                metricLabels,
                probeResult.value);
    }

    private static String appendDynamicLabel(ProbeResult probeResult) {
        if (!probeResult.dynamicLabelValues.isEmpty()) {
            return format(probeResult.probe.metricLabels, probeResult.dynamicLabelValues.toArray());
        } else {
            return probeResult.probe.metricLabels;
        }
    }
}
