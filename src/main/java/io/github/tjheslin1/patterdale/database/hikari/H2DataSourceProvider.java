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
package io.github.tjheslin1.patterdale.database.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.tjheslin1.patterdale.config.Passwords;
import io.github.tjheslin1.patterdale.config.RuntimeParameters;
import io.github.tjheslin1.patterdale.database.HikariDataSourceProvider;
import io.github.tjheslin1.patterdale.database.RetriableDataSource;
import io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition;
import org.slf4j.Logger;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

public class H2DataSourceProvider implements HikariDataSourceProvider {

    /**
     * Attempts to create a {@link HikariDataSource} by making an initial connection to the database.
     * Retries a number of times, with a delay between each retry, according to the provided {@link RuntimeParameters}.
     * <p>
     * Failed attempts are logged as well as when the number of retries has exceeded.
     *
     * @param runtimeParams      The app configuration defined in `patterdale.yml`.
     * @param databaseDefinition The details of the database being connected to.
     * @param passwords          The separate store of passwords, the matching password will be found according to the `databaseDefinition`.
     * @param logger             to log.
     * @return A data source for a successful connection to the database.
     */
    public HikariDataSource dataSource(RuntimeParameters runtimeParams, DatabaseDefinition databaseDefinition, Passwords passwords, Logger logger) {
        return RetriableDataSource.retriableDataSource(
                () -> ds(runtimeParams, databaseDefinition, passwords, logger),
                runtimeParams,
                databaseDefinition,
                logger);
    }

    private HikariDataSource ds(RuntimeParameters runtimeParameters, DatabaseDefinition databaseDefinition, Passwords passwords, Logger logger) {
        try {
            String password = passwords.byDatabaseName(databaseDefinition.name).value;
            return new HikariDataSource(jdbcConfig(runtimeParameters, databaseDefinition, password));
        } catch (Exception e) {
            logger.error("Error occurred initialising H2 and Hikari data sources.", e);
            throw e;    // caught by the RetryPolicy
        }
    }

    private static HikariConfig jdbcConfig(RuntimeParameters runtimeParameters, DatabaseDefinition databaseDefinition, String password) {
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setJdbcUrl(databaseDefinition.jdbcUrl);
        jdbcConfig.setUsername(databaseDefinition.user);
        jdbcConfig.setPassword(password);
        jdbcConfig.setPoolName("patterdale-pool-" + databaseDefinition.name);
        jdbcConfig.setMaximumPoolSize(runtimeParameters.connectionPoolMaxSize());
        jdbcConfig.setMinimumIdle(runtimeParameters.connectionPoolMinIdle());
        jdbcConfig.setInitializationFailTimeout(SECONDS.toMillis(runtimeParameters.probeConnectionWaitInSeconds()));

        jdbcConfig.addDataSourceProperty("cachePrepStmts", true);
        jdbcConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        jdbcConfig.addDataSourceProperty("useServerPrepStmts", true);

        return jdbcConfig;
    }
}
