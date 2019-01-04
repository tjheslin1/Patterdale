/*
 * Copyright 2019 Thomas Heslin <tjheslin1@kolabnow.com>.
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
package io.github.tjheslin1.patterdale.metrics;

import io.github.tjheslin1.patterdale.config.RuntimeParameters;
import io.github.tjheslin1.patterdale.metrics.probe.OracleSQLProbe;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.github.tjheslin1.patterdale.metrics.probe.ProbeResult.failedProbe;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public class MetricsUseCase {

    private final Logger logger;
    private final List<OracleSQLProbe> probes;
    private final RuntimeParameters runtimeParameters;
    private final Supplier<ExecutorService> executorServiceSupplier;

    public MetricsUseCase(Logger logger, List<OracleSQLProbe> probes, RuntimeParameters runtimeParameters, Supplier<ExecutorService> executorServiceSupplier) {
        this.logger = logger;
        this.probes = probes;
        this.runtimeParameters = runtimeParameters;
        this.executorServiceSupplier = executorServiceSupplier;
    }

    public List<ProbeResult> scrapeMetrics() {
        ExecutorService executor = executorServiceSupplier.get();

        List<ProbeWithFuture> eventualProbeResults = probes.stream()
                .map(probe -> new ProbeWithFuture(probe, executor.submit(new ScrapeProbe(probe))))
                .collect(toList());

        executor.shutdown();
        try {
            executor.awaitTermination(runtimeParameters.probeConnectionWaitInSeconds(), SECONDS);
        } catch (InterruptedException e) {
            logger.warn("Interrupted while awaiting termination", e);
            Thread.currentThread().interrupt();
        }
        try {
            return collectProbeResults(eventualProbeResults);
        } catch (Exception e) {
            logger.error("Exception occurred while scraping metrics", e);
            return failedProbeResults().collect(toList());
        }
    }

    private List<ProbeResult> collectProbeResults(List<ProbeWithFuture> eventualProbeResults) {
        return eventualProbeResults.stream()
                .flatMap(this::eventualResult)
                .collect(toList());
    }

    private Stream<ProbeResult> eventualResult(ProbeWithFuture eventualResult) {
        try {
            // the results are already there because of the await termination but we still need a timeout here for some reason
            return eventualResult.future.get(1, SECONDS).stream();
        } catch (Exception e) {
            logger.error("Exception occurred while scraping metrics for " + eventualResult.probe.probeDefinition(), e);
            return Stream.of(failedProbe(eventualResult.probe.probeDefinition()));
        }
    }

    private Stream<ProbeResult> failedProbeResults() {
        return probes.stream()
                .map(probe -> failedProbe(probe.probeDefinition()));
    }

    private static class ProbeWithFuture {
        public final OracleSQLProbe probe;
        public final Future<List<ProbeResult>> future;

        private ProbeWithFuture(OracleSQLProbe probe, Future<List<ProbeResult>> future) {
            this.probe = probe;
            this.future = future;
        }

    }
}