/*
 * Copyright 2018 Thomas Heslin <tjheslin1@gmail.com>.
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
package io.github.tjheslin1.patterdale.metrics.probe;

import io.github.tjheslin1.patterdale.ValueType;
import io.github.tjheslin1.patterdale.config.RuntimeParameters;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * {@link OracleSQLProbe} implementation which expects the provided SQL to return one row.
 * The probes checks the value of the first column and expects it to contain the integer '1'.
 * <p>
 * Anything other than a '1' is the first column, or no results returned at all, is treated as a failure.
 */
public class ExistsOracleSQLProbe extends ValueType implements OracleSQLProbe {

    private final Probe probe;
    private final Future<DBConnectionPool> connectionPool;
    private final RuntimeParameters runtimeParameters;
    private final Logger logger;

    public ExistsOracleSQLProbe(Probe probe, Future<DBConnectionPool> connectionPool, RuntimeParameters runtimeParameters, Logger logger) {
        this.probe = probe;
        this.connectionPool = connectionPool;
        this.runtimeParameters = runtimeParameters;
        this.logger = logger;
    }

    @Override
    public Probe probeDefinition() {
        return probe;
    }

    /**
     * @return a single {@link ProbeResult} with a metric value of 1.0 for a successful probes,
     * a value of 0.0 for a failed probes or a value of -1.0 if the probes was unable to query the database.
     */
    @Override
    public List<ProbeResult> probes() {
        int timeout = runtimeParameters.probeConnectionWaitInSeconds();
        try (Connection connection = connectionPool.get(timeout, SECONDS)
                .pool().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(probe.query())) {
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return singletonList(new ProbeResult(0, probe));
            }

            int result = resultSet.getInt(1);
            if (result != 1) {
                return singletonList(new ProbeResult(0, probe));
            }

            return singletonList(new ProbeResult(1, probe));
        } catch (TimeoutException timeoutEx) {
            logger.warn(format("Timed out waiting for connection for probes '%s' after '%d' seconds", probe.name, timeout));
            return singletonList(new ProbeResult(-1, probe));
        } catch (Exception e) {
            String message = format("Error occurred executing query: '%s'", probe.query());
            logger.error(message, e);
            return singletonList(new ProbeResult(-1, probe));
        }
    }
}
