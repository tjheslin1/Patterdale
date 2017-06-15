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
import io.github.tjheslin1.patterdale.metrics.OracleSQLProbe;
import io.github.tjheslin1.patterdale.metrics.ProbeResult;
import io.github.tjheslin1.patterdale.metrics.SQLProbe;
import oracle.jdbc.pool.OracleDataSource;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

import static io.github.tjheslin1.patterdale.PatterdaleRuntimeParameters.patterdaleRuntimeParameters;
import static io.github.tjheslin1.patterdale.metrics.ProbeResult.failure;
import static io.github.tjheslin1.patterdale.metrics.ProbeResult.success;
import static java.lang.String.format;
import static java.util.Collections.singletonList;

public class Patterdale {

    private static final String HEALTH_CHECK_SQL = "SELECT 1 FROM DUAL";

    private final PatterdaleRuntimeParameters runtimeParameters;
    private final Logger logger;

    public Patterdale(PatterdaleRuntimeParameters runtimeParameters, Logger logger) {
        this.runtimeParameters = runtimeParameters;
        this.logger = logger;
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("io.github.tjheslin1.patterdale.Patterdale");

        PatterdaleConfig patterdaleConfig = new ConfigUnmarshaller(logger)
                .parseConfig(new File(System.getProperty("config.file")));

        Patterdale patterdale = new Patterdale(patterdaleRuntimeParameters(patterdaleConfig), logger);
        logger.debug("starting Patterdale!");

        patterdale.start();
    }

    public void start() {
        System.setProperty("logback.configurationFile", runtimeParameters.logbackConfiguration());

        HikariDataSource hikariDataSource = dataSource();
        DBConnectionPool connectionPool = new HikariDBConnectionPool(new HikariDBConnection(hikariDataSource));

        Server server = new Server(runtimeParameters.httpPort());
        List<SQLProbe> probes = singletonList(new OracleSQLProbe(HEALTH_CHECK_SQL, healthCheck(), connectionPool, logger));
        WebServer webServer = new JettyWebServerBuilder()
                .withServer(server)
                .registerMetricsEndpoint("/metrics", new MetricsUseCase(probes))
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

    private Function<ResultSet, ProbeResult> healthCheck() {
        return (rs) -> {
            try {
                int result = rs.getInt(1);
                if (result != 1) {
                    return failure(format("Expected a result of '1' from SQL query '%s' but got '%s'",
                            HEALTH_CHECK_SQL, result));
                }
                return success("Successful health check.");
            } catch (SQLException e) {
                String message = format("Error occurred executing sql: '%s'", HEALTH_CHECK_SQL);
                logger.error(message);
                return failure(message);
            }
        };
    }

    private HikariDataSource dataSource() {
        try {
            OracleDataSource oracleDataSource = new OracleDataSource();
            oracleDataSource.setServerName(runtimeParameters.databaseServerName());
            oracleDataSource.setDatabaseName(runtimeParameters.databaseName());
            oracleDataSource.setNetworkProtocol(runtimeParameters.databaseNetworkProtocol());
            oracleDataSource.setPortNumber(runtimeParameters.httpPort());
            oracleDataSource.setDriverType(runtimeParameters.driverType());

            HikariDataSource hikariDataSource = new HikariDataSource(jdbcConfig());
            hikariDataSource.setDataSource(oracleDataSource);
            return hikariDataSource;
        } catch (Exception e) {
            logger.error("Error occurred initialising Oracle and Hikari data sources.", e);
            throw new IllegalStateException(e);
        }
    }

    private HikariConfig jdbcConfig() {
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setPoolName("patterdale-pool");
        jdbcConfig.setMaximumPoolSize(runtimeParameters.connectionPoolMaxSize());
        jdbcConfig.setMinimumIdle(runtimeParameters.connectionPoolMinIdle());
        jdbcConfig.setJdbcUrl(runtimeParameters.databaseJdbcUrl());
        return jdbcConfig;
    }
}
