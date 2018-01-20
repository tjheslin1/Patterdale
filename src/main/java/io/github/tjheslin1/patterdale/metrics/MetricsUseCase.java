/*
 * Copyright 2018 Thomas Heslin <tjheslin1@gmail.com>.
 *
 * This file is part of Patterdale-jvm.
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
package io.github.tjheslin1.patterdale.metrics;

import io.github.tjheslin1.patterdale.metrics.probe.OracleSQLProbe;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static io.github.tjheslin1.patterdale.metrics.probe.ProbeResult.failedProbe;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public class MetricsUseCase {

    private final List<OracleSQLProbe> probes;

    public MetricsUseCase(List<OracleSQLProbe> probes) {
        this.probes = probes;
    }

    public List<ProbeResult> scrapeMetrics() {
        ExecutorService executor = Executors.newFixedThreadPool(probes.size());

        List<Future<List<ProbeResult>>> eventualProbeResults = probes.stream()
                .map(probe -> executor.submit(() -> executeProbe(probe)))
                .collect(toList());

        try {
            executor.awaitTermination(10, SECONDS);
            return collectProbeResultsWithTimeout(0, eventualProbeResults);
        } catch (InterruptedException e) {
            return failedProbeResults().collect(toList());
        }
    }

    private List<ProbeResult> executeProbe(OracleSQLProbe oracleSQLProbe) {
        return oracleSQLProbe.probes();
    }

    private List<ProbeResult> collectProbeResultsWithTimeout(int timeout, List<Future<List<ProbeResult>>> eventualProbeResults) {
        return eventualProbeResults.stream()
                .flatMap(probe -> eventualResult(timeout, probe))
                .collect(toList());
    }

    private Stream<ProbeResult> eventualResult(int timeout, Future<List<ProbeResult>> eventualResult) {
        try {
            return eventualResult.get(timeout, SECONDS).stream();
        } catch (Exception e) {
            return failedProbeResults();
        }
    }

    private Stream<ProbeResult> failedProbeResults() {
        return probes.stream()
                .map(probe -> failedProbe(probe.probeDefinition()));
    }
}

