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
package io.github.tjheslin1.patterdale.infrastructure;

import io.github.tjheslin1.patterdale.metrics.JettyStatisticsCollector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.hotspot.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.StatisticsHandler;

public class RegisterExporters {

    /**
     * @param registry Prometheus CollectorRegistry to register the default exporters.
     * @param httpPort The port the Server runs on.
     * @return a Jetty Server with Prometheus' default exporters registered.
     */
    public static Server serverWithStatisticsCollection(CollectorRegistry registry, int httpPort) {
        Server server = new Server(httpPort);

        new StandardExports().register(registry);
        new MemoryPoolsExports().register(registry);
        new GarbageCollectorExports().register(registry);
        new ThreadExports().register(registry);
        new ClassLoadingExports().register(registry);
        new VersionInfoExports().register(registry);

        HandlerCollection handlers = new HandlerCollection();
        StatisticsHandler statisticsHandler = new StatisticsHandler();
        statisticsHandler.setServer(server);
        handlers.addHandler(statisticsHandler);

        new JettyStatisticsCollector(statisticsHandler).register(registry);
        server.setHandler(handlers);

        return server;
    }
}
