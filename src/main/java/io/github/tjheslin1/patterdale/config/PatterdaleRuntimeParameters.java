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
package io.github.tjheslin1.patterdale.config;

import io.github.tjheslin1.patterdale.ValueType;
import io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition;
import io.github.tjheslin1.patterdale.metrics.probe.Probe;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * The runtime configuration of the app. Configured by the 'patterdale.yml' file passed in on app start up.
 * This is a flatted representation of {@link PatterdaleConfig}.
 */
public class PatterdaleRuntimeParameters extends ValueType implements RuntimeParameters {

    private final int httpPort;
    private final long cacheDuration;
    private final List<DatabaseDefinition> databases;
    private final List<Probe> probes;
    private final int connectionPoolMaxSize;
    private final int connectionPoolMinIdle;
    private final int maxConnectionRetries;
    private final long connectionRetryDelayInSeconds;

    public PatterdaleRuntimeParameters(int httpPort, long cacheDuration, List<DatabaseDefinition> databases, int connectionPoolMaxSize, int connectionPoolMinIdle, List<Probe> probes, int maxConnectionRetries, long connectionRetryDelayInSeconds) {
        this.httpPort = httpPort;
        this.cacheDuration = cacheDuration;
        this.databases = databases;
        this.connectionPoolMaxSize = connectionPoolMaxSize;
        this.connectionPoolMinIdle = connectionPoolMinIdle;
        this.probes = probes;
        this.maxConnectionRetries = maxConnectionRetries;
        this.connectionRetryDelayInSeconds = connectionRetryDelayInSeconds;
    }

    public static PatterdaleRuntimeParameters patterdaleRuntimeParameters(PatterdaleConfig config) {
        return new PatterdaleRuntimeParameters(
                config.httpPort,
                config.cacheDuration,
                asList(config.databases),
                integerParameterOrDefault(config.connectionPool, "maxSize", 5),
                integerParameterOrDefault(config.connectionPool, "minIdle", 1),
                asList(config.probes),
                integerParameterOrDefault(config.connectionPool, "maxConnectionRetries", 10),
                integerParameterOrDefault(config.connectionPool, "connectionRetryDelayInSeconds", 60));
    }

    /**
     * @return the HTTP port the jetty server is exposed on.
     */
    @Override
    public int httpPort() {
        return httpPort;
    }

    /**
     * Configures the cache of {@link io.github.tjheslin1.patterdale.metrics.probe.ProbeResult}'s which is populated
     * when the {@link io.github.tjheslin1.patterdale.metrics.probe.OracleSQLProbe}'s are executed.
     * <p>
     * This is to prevent the application hammering the database with queries.
     *
     * @return The lifetime of the cache of in seconds.
     */
    @Override
    public long cacheDuration() {
        return cacheDuration;
    }

    /**
     * @return The listed database definitions.
     */
    @Override
    public List<DatabaseDefinition> databases() {
        return databases;
    }

    /**
     * @return The listed probe definitions.
     */
    @Override
    public List<Probe> probes() {
        return probes;
    }

    /**
     * @return The max size of the connection pools for the databases.
     */
    @Override
    public int connectionPoolMaxSize() {
        return connectionPoolMaxSize;
    }

    /**
     * @return The minimum size of the connection pools for the databases.
     */
    @Override
    public int connectionPoolMinIdle() {
        return connectionPoolMinIdle;
    }

    /**
     * @return The number of retry attempts to connect to each database.
     */
    @Override
    public int maxConnectionRetries() {
        return maxConnectionRetries;
    }

    /**
     * @return The delay between database connection retries.
     */
    @Override
    public long connectionRetryDelayInSeconds() {
        return connectionRetryDelayInSeconds;
    }

    private static int integerParameterOrDefault(Map<String, String> config, String parameter, int defaultValue) {
        if (config == null) {
            throw new IllegalStateException(format("Parent value of field '%s' not present in config.file provided.", parameter));
        }
        String paramValue = config.get(parameter);
        if (paramValue == null) {
            return defaultValue;
        }
        return Integer.parseInt(paramValue);
    }
}
