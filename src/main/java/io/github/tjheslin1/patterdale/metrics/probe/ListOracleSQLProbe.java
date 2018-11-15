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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * {@link OracleSQLProbe} implementation which expects the provided SQL to return one or more rows, with at least 2 columns.
 * The first column is expected to be a Double to be assigned as the metric value.
 * The second column and onwards represent the metric label values which will be filtered using `java.lang.String#format`
 *  into the `metricLabels` provided in this probes' config.
 *
 *  If the probe fails or times out, an empty list of {@link ProbeResult} will be returned.
 */
public class ListOracleSQLProbe extends ValueType implements OracleSQLProbe {

    private static final int DYNAMIC_LABELS_START_INDEX = 2;

    private final Probe probe;
    private final Future<DBConnectionPool> connectionPool;
    private final RuntimeParameters runtimeParameters;
    private final Logger logger;

    public ListOracleSQLProbe(Probe probe, Future<DBConnectionPool> connectionPool, RuntimeParameters runtimeParameters, Logger logger) {
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
     * @return a List of {@link ProbeResult}. Each {@link ProbeResult} represents a
     * row returned from the provided SQL or empty if an error occurred.
     */
    @Override
    public List<ProbeResult> probes() {
        int timeout = runtimeParameters.probeConnectionWaitInSeconds();
        try (Connection connection = connectionPool.get(timeout, SECONDS)
                .pool().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(probe.query())) {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<ProbeResult> probeResults = new ArrayList<>();
            while (resultSet.next()) {
                int columnCount = resultSet.getMetaData().getColumnCount();
                double metricValue = resultSet.getDouble(1);

                List<String> dynamicLabels = new ArrayList<>(columnCount - 1);
                for (int columnIndex = DYNAMIC_LABELS_START_INDEX; columnIndex <= columnCount; columnIndex++) {
                    String dynamicLabel = resultSet.getString(columnIndex).replaceAll("\\s+", " ");
                    dynamicLabels.add(dynamicLabel);
                }

                probeResults.add(new ProbeResult(metricValue, probe, dynamicLabels));
            }

            return probeResults;
        } catch (TimeoutException timeoutEx) {
            logger.warn(format("Timed out waiting for connection for probes '%s' after '%d' seconds", probe.name, timeout));
            return emptyList();
        } catch (Exception e) {
            logger.error(format("Error occurred executing query: '%s'", probe.query()), e);
            return emptyList();
        }
    }
}
