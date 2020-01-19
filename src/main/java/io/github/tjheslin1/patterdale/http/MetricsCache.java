/*
 * Copyright 2017 - 2020 Thomas Heslin <tjheslin1@kolabnow.com>.
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
package io.github.tjheslin1.patterdale.http;

import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class MetricsCache implements Supplier<List<ProbeResult>>, Runnable {

    private final AtomicReference<List<ProbeResult>> results = new AtomicReference<>();
    private final MetricsUseCase metricsUseCase;

    private MetricsCache(MetricsUseCase metricsUseCase) {
        this.metricsUseCase = metricsUseCase;
    }

    public static MetricsCache metricsCache(MetricsUseCase metricsUseCase, long cacheDuration) {
        MetricsCache metricsCache = new MetricsCache(metricsUseCase);
        metricsCache.run();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(metricsCache, cacheDuration, cacheDuration, TimeUnit.SECONDS);
        return metricsCache;
    }

    @Override
    public List<ProbeResult> get() {
        return results.get();
    }

    @Override
    public void run() {
        results.set(metricsUseCase.scrapeMetrics());
    }
}
