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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import io.github.tjheslin1.patterdale.database.hikari.HikariDBConnection;
import io.github.tjheslin1.patterdale.database.hikari.HikariDBConnectionPool;
import io.github.tjheslin1.patterdale.http.WebServer;
import io.github.tjheslin1.patterdale.http.jetty.JettyWebServerBuilder;
import io.github.tjheslin1.patterdale.metrics.MetricsUseCase;
import io.github.tjheslin1.patterdale.metrics.probe.OracleSQLProbe;
import io.github.tjheslin1.patterdale.metrics.probe.Probe;
import io.github.tjheslin1.patterdale.metrics.probe.TypeToProbeMapper;
import oracle.jdbc.pool.OracleDataSource;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static io.github.tjheslin1.patterdale.PatterdaleRuntimeParameters.patterdaleRuntimeParameters;
import static java.util.stream.Collectors.toList;

public class Patterdale {

    private final PatterdaleRuntimeParameters runtimeParameters;
    private final TypeToProbeMapper typeToProbeMapper;
    private final Logger logger;

    public Patterdale(PatterdaleRuntimeParameters runtimeParameters, TypeToProbeMapper typeToProbeMapper, Logger logger) {
        this.runtimeParameters = runtimeParameters;
        this.typeToProbeMapper = typeToProbeMapper;
        this.logger = logger;
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("io.github.tjheslin1.patterdale.Patterdale");

        PatterdaleConfig patterdaleConfig = new ConfigUnmarshaller(logger)
                .parseConfig(new File(System.getProperty("config.file")));

        PatterdaleRuntimeParameters runtimeParameters = patterdaleRuntimeParameters(patterdaleConfig);

        HikariDataSource hikariDataSource = dataSource(runtimeParameters, logger);
        DBConnectionPool dbConnectionPool = new HikariDBConnectionPool(new HikariDBConnection(hikariDataSource));

        Patterdale patterdale = new Patterdale(runtimeParameters, new TypeToProbeMapper(dbConnectionPool, logger), logger);
        logger.debug("starting Patterdale!");

        patterdale.start();
    }

    public void start() {
        System.setProperty("logback.configurationFile", "src/main/resources/logback.xml");

        Server server = new Server(runtimeParameters.httpPort());

        List<OracleSQLProbe> probes = runtimeParameters.databases().stream()
                .flatMap(database -> Arrays.stream(database.probes))
                .map(this::createProbe)
                .collect(toList());

        WebServer webServer = new JettyWebServerBuilder(logger)
                .withServer(server)
                .registerMetricsEndpoint("/metrics", new MetricsUseCase(probes), runtimeParameters)
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

    private OracleSQLProbe createProbe(Probe probe) {
        return typeToProbeMapper.createProbe(probe);
    }


    public static HikariDataSource dataSource(PatterdaleRuntimeParameters runtimeParameters, Logger logger) {
        try {
            OracleDataSource oracleDataSource = new OracleDataSource();
            oracleDataSource.setUser(runtimeParameters.databases().get(0).user);
            oracleDataSource.setPassword(runtimeParameters.databases().get(0).password);

            HikariDataSource hikariDataSource = new HikariDataSource(jdbcConfig(runtimeParameters));
            hikariDataSource.setDataSource(oracleDataSource);
            return hikariDataSource;
        } catch (Exception e) {
            logger.error("Error occurred initialising Oracle and Hikari data sources.", e);
            throw new IllegalStateException(e);
        }
    }

    private static HikariConfig jdbcConfig(PatterdaleRuntimeParameters runtimeParameters) {
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setPoolName("patterdale-pool");
        jdbcConfig.setMaximumPoolSize(runtimeParameters.connectionPoolMaxSize());
        jdbcConfig.setMinimumIdle(runtimeParameters.connectionPoolMinIdle());
        jdbcConfig.setJdbcUrl(runtimeParameters.databases().get(0).jdbcUrl);
        return jdbcConfig;
    }
}
