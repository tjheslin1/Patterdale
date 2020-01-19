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
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.slf4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Supplier;

import static io.github.tjheslin1.patterdale.http.MetricsCache.metricsCache;
import static io.github.tjheslin1.patterdale.metrics.probe.ProbeResultFormatter.formatProbeResults;

public class MetricsServlet extends HttpServlet {

    private final CollectorRegistry registry;
    private final Supplier<List<ProbeResult>> metricsCache;
    private final Logger logger;

    public MetricsServlet(CollectorRegistry registry, MetricsUseCase metricsUseCase, Logger logger, long cacheDuration) {
        this.registry = registry;
        this.metricsCache = metricsCache(metricsUseCase, cacheDuration);
        this.logger = logger;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(TextFormat.CONTENT_TYPE_004);

        List<ProbeResult> probeResults = metricsCache.get();

        formatProbeResults(probeResults)
                .forEach(formattedProbeResult -> {
                    try {
                        resp.getWriter().print(formattedProbeResult + "\n");
                    } catch (IOException e) {
                        logger.error("IO error occurred writing to /metrics page.", e);
                    }
                });

        try (Writer writer = resp.getWriter()) {
            TextFormat.write004(writer, registry.filteredMetricFamilySamples(parse(req)));
            writer.flush();
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        doGet(req, resp);
    }

    private Set<String> parse(HttpServletRequest req) {
        String[] includedParam = req.getParameterValues("name[]");
        if (includedParam == null) {
            return Collections.emptySet();
        } else {
            return new HashSet<>(Arrays.asList(includedParam));
        }
    }
}
