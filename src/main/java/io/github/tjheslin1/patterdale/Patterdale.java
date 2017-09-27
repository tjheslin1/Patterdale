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
package io.github.tjheslin1.patterdale;

import io.github.tjheslin1.patterdale.config.ConfigUnmarshaller;
import io.github.tjheslin1.patterdale.config.Passwords;
import io.github.tjheslin1.patterdale.config.PasswordsUnmarshaller;
import io.github.tjheslin1.patterdale.config.PatterdaleConfig;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import io.github.tjheslin1.patterdale.database.hikari.HikariDBConnection;
import io.github.tjheslin1.patterdale.database.hikari.HikariDBConnectionPool;
import io.github.tjheslin1.patterdale.database.hikari.HikariDataSourceProvider;
import io.github.tjheslin1.patterdale.http.WebServer;
import io.github.tjheslin1.patterdale.http.jetty.JettyWebServerBuilder;
import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition;
import io.github.tjheslin1.patterdale.metrics.probe.OracleSQLProbe;
import io.github.tjheslin1.patterdale.metrics.probe.TypeToProbeMapper;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.tjheslin1.patterdale.PatterdaleRuntimeParameters.patterdaleRuntimeParameters;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class Patterdale {

    private final PatterdaleRuntimeParameters runtimeParameters;
    private final Map<String, DBConnectionPool> connectionPools;
    private final TypeToProbeMapper typeToProbeMapper;
    private final Logger logger;

    public Patterdale(PatterdaleRuntimeParameters runtimeParameters, Map<String, DBConnectionPool> connectionPools, TypeToProbeMapper typeToProbeMapper, Logger logger) {
        this.runtimeParameters = runtimeParameters;
        this.connectionPools = connectionPools;
        this.typeToProbeMapper = typeToProbeMapper;
        this.logger = logger;
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("io.github.tjheslin1.patterdale.Patterdale");

        PatterdaleConfig patterdaleConfig = new ConfigUnmarshaller(logger)
                .parseConfig(new File(System.getProperty("config.file")));

        Passwords passwords = new PasswordsUnmarshaller(logger)
                .parsePasswords(new File(System.getProperty("passwords.file")));

        PatterdaleRuntimeParameters runtimeParameters = patterdaleRuntimeParameters(patterdaleConfig);

        Map<String, DBConnectionPool> connectionPools = runtimeParameters.databases().stream()
                .collect(Collectors.toMap(databaseDefinition -> databaseDefinition.name,
                        databaseDefinition -> new HikariDBConnectionPool(new HikariDBConnection(HikariDataSourceProvider.dataSource(runtimeParameters, databaseDefinition, passwords, logger)))));

        Patterdale patterdale = new Patterdale(runtimeParameters, connectionPools, new TypeToProbeMapper(logger), logger);
        logger.debug("starting Patterdale!");

        patterdale.start();
    }

    public void start() {
        System.setProperty("logback.configurationFile", "src/main/resources/logback.xml");

        Server server = new Server(runtimeParameters.httpPort());

        List<OracleSQLProbe> probes = runtimeParameters.databases().stream()
                .flatMap(this::createProbes)
                .collect(toList());

        long cacheDuration = Math.max(runtimeParameters.cacheDuration(), 1);
        logger.info(format("Using cache duration of %d seconds.", cacheDuration));

        WebServer webServer = new JettyWebServerBuilder(logger)
                .withServer(server)
                .registerMetricsEndpoint("/metrics", new MetricsUseCase(probes), runtimeParameters, cacheDuration)
                .build();

        try {
            webServer.start();
            logger.info("Web server started successfully at " + webServer.baseUrl());
        } catch (Exception e) {
            logger.error("Error occurred starting Jetty Web Server.", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                webServer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    private Stream<OracleSQLProbe> createProbes(DatabaseDefinition databaseDefinition) {
        return Arrays.stream(databaseDefinition.probes)
                .map(probe -> typeToProbeMapper.createProbe(connectionPools.get(databaseDefinition.name), probe));
    }
}
