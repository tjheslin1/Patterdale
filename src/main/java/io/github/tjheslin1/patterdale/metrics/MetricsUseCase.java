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

import io.github.tjheslin1.patterdale.config.RuntimeParameters;
import io.github.tjheslin1.patterdale.metrics.probe.OracleSQLProbe;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.github.tjheslin1.patterdale.metrics.probe.ProbeResult.failedProbe;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public class MetricsUseCase {

    private final List<OracleSQLProbe> probes;
    private final RuntimeParameters runtimeParameters;
    private final Supplier<ExecutorService> executorServiceSupplier;

    public MetricsUseCase(List<OracleSQLProbe> probes, RuntimeParameters runtimeParameters, Supplier<ExecutorService> executorServiceSupplier) {
        this.probes = probes;
        this.runtimeParameters = runtimeParameters;
        this.executorServiceSupplier = executorServiceSupplier;
    }

    public List<ProbeResult> scrapeMetrics() {
        ExecutorService executor = executorServiceSupplier.get();

        List<Future<List<ProbeResult>>> eventualProbeResults = probes.stream()
                .map(probe -> executor.submit(new ScrapeProbe(probe)))
                .collect(toList());

        try {
            executor.shutdown();
            executor.awaitTermination(runtimeParameters.probeConnectionWaitInSeconds(), SECONDS);
            return collectProbeResults(eventualProbeResults);
        } catch (InterruptedException e) {
            return failedProbeResults().collect(toList());
        }
    }

    private List<ProbeResult> collectProbeResults(List<Future<List<ProbeResult>>> eventualProbeResults) {
        return eventualProbeResults.stream()
                .flatMap(probe -> eventualResult(1, probe))
                .collect(toList());
    }

    private Stream<ProbeResult> eventualResult(int timeout, Future<List<ProbeResult>> eventualResult) {
        try {
            return eventualResult.get(timeout, SECONDS).stream();
        } catch (Exception e) {
            return Stream.empty();
        }
    }

    private Stream<ProbeResult> failedProbeResults() {
        return probes.stream()
                .map(probe -> failedProbe(probe.probeDefinition()));
    }
}