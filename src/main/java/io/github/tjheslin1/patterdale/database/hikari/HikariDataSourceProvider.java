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
package io.github.tjheslin1.patterdale.database.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.tjheslin1.patterdale.PatterdaleRuntimeParameters;
import io.github.tjheslin1.patterdale.config.Passwords;
import io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition;
import oracle.jdbc.pool.OracleDataSource;
import org.slf4j.Logger;

import static io.github.tjheslin1.patterdale.database.JdbcUrlFormatter.databaseUrlWithCredentials;

public class HikariDataSourceProvider {

    public static HikariDataSource dataSource(PatterdaleRuntimeParameters runtimeParameters, DatabaseDefinition databaseDefinition, Passwords passwords, Logger logger) {
        try {
            OracleDataSource oracleDataSource = new OracleDataSource();
            oracleDataSource.setUser(databaseDefinition.user);

            String password = passwords.byDatabaseName(databaseDefinition.name).value;
            oracleDataSource.setPassword(password);

            HikariDataSource hikariDataSource = new HikariDataSource(jdbcConfig(runtimeParameters, databaseDefinition, password));
            hikariDataSource.setDataSource(oracleDataSource);
            return hikariDataSource;
        } catch (Exception e) {
            logger.error("Error occurred initialising Oracle and Hikari data sources.", e);
            throw new IllegalStateException(e);
        }
    }

    private static HikariConfig jdbcConfig(PatterdaleRuntimeParameters runtimeParameters, DatabaseDefinition databaseDefinition, String password) {
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setPoolName("patterdale-pool-" + databaseDefinition.name);
        jdbcConfig.setMaximumPoolSize(runtimeParameters.connectionPoolMaxSize());
        jdbcConfig.setMinimumIdle(runtimeParameters.connectionPoolMinIdle());
        jdbcConfig.setJdbcUrl(databaseUrlWithCredentials(databaseDefinition.jdbcUrl, databaseDefinition.user, password));
        return jdbcConfig;
    }
}
