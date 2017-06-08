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
import oracle.jdbc.pool.OracleDataSource;
import org.eclipse.jetty.server.Server;

import java.sql.SQLException;

public class Patterdale {

    public static void main(String[] args) {
        System.out.println("Patterdale!");
        start();
    }

    public static void start() {
        HikariDataSource hikariDataSource = dataSource();
        DBConnectionPool connectionPool = new HikariDBConnectionPool(new HikariDBConnection(hikariDataSource));

        Server server = new Server(7000);
        WebServer webServer = new JettyWebServerBuilder()
                .withServer(server)
                .registerMetricsEndpoint("/metrics", new MetricsUseCase(connectionPool))
                .build();

        try {
            webServer.start();
        } catch (Exception e) {
            System.out.println("Error occurred starting Jetty Web Server.");
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    webServer.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static HikariDataSource dataSource() {
        try {
            OracleDataSource oracleDataSource = new OracleDataSource();
            oracleDataSource.setServerName("primary");
            oracleDataSource.setDatabaseName("dual");
            oracleDataSource.setNetworkProtocol("tcp");
            oracleDataSource.setPortNumber(1521);
            oracleDataSource.setDriverType("thin");

            HikariDataSource hikariDataSource = new HikariDataSource(jdbcConfig());
            hikariDataSource.setDataSource(oracleDataSource);
            return hikariDataSource;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static HikariConfig jdbcConfig() {
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setPoolName("patterdale-pool");
        jdbcConfig.setMaximumPoolSize(5);
        jdbcConfig.setMinimumIdle(2);
        jdbcConfig.setJdbcUrl("jdbc:oracle:thin:system/oracle@localhost:1521:xe");
        return jdbcConfig;
    }
}
