/*
 * Copyright 2017 Thomas Heslin <tjheslin1@gmail.com>.
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
package io.github.tjheslin1.patterdale.http;

import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static io.github.tjheslin1.patterdale.metrics.probe.ProbeResultFormatter.formatProbeResults;

public class MetricsServlet extends HttpServlet {

    private final MetricsUseCase metricsUseCase;
    private final Logger logger;

    public MetricsServlet(MetricsUseCase metricsUseCase, Logger logger) {
        this.metricsUseCase = metricsUseCase;
        this.logger = logger;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ProbeResult> probeResults = metricsUseCase.scrapeMetrics();

        formatProbeResults(probeResults)
                .forEach(formattedProbeResult -> {
                    try {
                        resp.getWriter().println(formattedProbeResult);
                    } catch (IOException e) {
                        logger.error("IO error occurred writing to /metrics page.", e);
                    }
                });
    }

}
