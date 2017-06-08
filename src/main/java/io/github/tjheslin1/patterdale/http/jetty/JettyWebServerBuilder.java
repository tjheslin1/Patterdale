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
package io.github.tjheslin1.patterdale.http.jetty;

import io.github.tjheslin1.patterdale.http.MetricsServlet;
import io.github.tjheslin1.patterdale.http.NotFoundServlet;
import io.github.tjheslin1.patterdale.http.WebServer;
import io.github.tjheslin1.patterdale.http.WebServerBuilder;
import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyWebServerBuilder implements WebServerBuilder {

    private final ServletContextHandler servletContextHandler = new ServletContextHandler();
    private Server server;

    @Override
    public WebServerBuilder registerMetricsEndpoint(String path, MetricsUseCase metricsUseCase) {
        servletContextHandler.addServlet(new ServletHolder(new MetricsServlet(metricsUseCase)), path);
        return this;
    }

    public WebServerBuilder withServer(Server server) {
        this.server = server;
        return this;
    }

    @Override
    public WebServer build() {
        System.out.println("About to build");
        if (server == null) {
            throw new IllegalStateException("Server was not set!");
        }

        servletContextHandler.addServlet(new ServletHolder(new NotFoundServlet()), "/");
        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);
        server.addBean(new UncaughtErrorHandler());
        return new JettyWebServer(server);
    }
}
