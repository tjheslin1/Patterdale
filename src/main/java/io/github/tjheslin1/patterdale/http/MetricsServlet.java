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

import io.github.tjheslin1.patterdale.RuntimeParameters;
import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import io.github.tjheslin1.patterdale.metrics.ProbeDefinition;
import io.github.tjheslin1.patterdale.metrics.ProbeResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class MetricsServlet extends HttpServlet {

    private final MetricsUseCase metricsUseCase;
    private final RuntimeParameters runtimeParameters;

    public MetricsServlet(MetricsUseCase metricsUseCase, RuntimeParameters runtimeParameters) {
        this.metricsUseCase = metricsUseCase;
        this.runtimeParameters = runtimeParameters;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<ProbeDefinition, ProbeResult> probeResults = metricsUseCase.scrapeMetrics();

//        boolean success = probeResults.stream().allMatch(probeResult -> probeResult.result);

//        resp.getWriter().print(format("%s{%s} 1", "TODO", "TODO"));
//        resp.getWriter().print(format("%s{%s} 0", "TODO", "TODO"));

//        for (ProbeResult probeResult : probeResults) {
//            resp.getWriter().print(format("%s{%s} %s", "TODO", "TODO", probeResult.result ? 1 : 0));
//        }
//
//        if (!success) {
//            resp.setStatus(500);
//        }
    }
}
