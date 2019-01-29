/*
 * Copyright 2019 Thomas Heslin <tjheslin1@kolabnow.com>.
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
package io.github.tjheslin1.patterdale.database;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import io.github.tjheslin1.patterdale.config.RuntimeParameters;
import io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;

import java.util.concurrent.Callable;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

public class RetriableDataSource {

    public static HikariDataSource retriableDataSource(Callable<HikariDataSource> dataSource,
                                                       RuntimeParameters runtimeParams,
                                                       DatabaseDefinition databaseDefinition,
                                                       Logger logger) {
        RetryPolicy retryPolicy = new RetryPolicy()
                .retryOn(HikariPool.PoolInitializationException.class)
                .withDelay(runtimeParams.connectionRetryDelayInSeconds(), SECONDS)
                .withMaxRetries(runtimeParams.maxConnectionRetries());

        return Failsafe.with(retryPolicy)
                .onRetry((result, failure, context) -> logRetry(runtimeParams, databaseDefinition, logger))
                .onFailedAttempt((result, failure, context) -> logFailedAttempt(databaseDefinition, logger))
                .onRetriesExceeded(throwable -> logRetriesExceeded(databaseDefinition, logger))
                .get(dataSource);
    }

    public static void logRetriesExceeded(DatabaseDefinition databaseDefinition, Logger logger) {
        logger.error(format("Exceeded retry attempts to database %s at %s.",
                databaseDefinition.name,
                databaseDefinition.jdbcUrl));
    }

    public static void logFailedAttempt(DatabaseDefinition databaseDefinition, Logger logger) {
        logger.warn(format("Failed attempt connecting to database %s at %s." +
                        databaseDefinition.name,
                databaseDefinition.jdbcUrl));
    }

    public static void logRetry(RuntimeParameters runtimeParams,
                                DatabaseDefinition databaseDefinition,
                                Logger logger) {
        logger.info(format("Attempting database connection to: %s at %s.%n" +
                        "Configured to retry %d times with a delay between retries of %d seconds.",
                databaseDefinition.name,
                databaseDefinition.jdbcUrl,
                runtimeParams.maxConnectionRetries(),
                runtimeParams.connectionRetryDelayInSeconds()));
    }
}
