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

import io.github.tjheslin1.patterdale.metrics.ProbeDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class PatterdaleRuntimeParameters extends ValueType implements RuntimeParameters {

    private final int httpPort;
    private final String databaseUser;
    private final String databasePassword;
    private final String jdbcUrl;
    private final int connectionPoolMaxSize;
    private final int connectionPoolMinIdle;
    private final List<ProbeDefinition> probes;

    PatterdaleRuntimeParameters(int httpPort, String databaseUser, String databasePassword, String jdbcUrl, int connectionPoolMaxSize, int connectionPoolMinIdle, List<ProbeDefinition> probes) {
        this.httpPort = httpPort;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
        this.jdbcUrl = jdbcUrl;
        this.connectionPoolMaxSize = connectionPoolMaxSize;
        this.connectionPoolMinIdle = connectionPoolMinIdle;
        this.probes = probes;
    }

    public static PatterdaleRuntimeParameters patterdaleRuntimeParameters(PatterdaleConfig config) {
        return new PatterdaleRuntimeParameters(
                config.httpPort,
                parameterOrBlowUp(config.database, "user"),
                parameterOrBlowUp(config.database, "password"),
                parameterOrBlowUp(config.database, "jdbcUrl"),
                integerParameterOrBlowUp(config.connectionPool, "maxSize"),
                integerParameterOrBlowUp(config.connectionPool, "minIdle"),
                readProbes(config.probes));
    }

    @Override
    public int httpPort() {
        return httpPort;
    }

    @Override
    public String databaseUser() {
        return databaseUser;
    }

    @Override
    public String databasePassword() {
        return databasePassword;
    }

    @Override
    public String databaseJdbcUrl() {
        return jdbcUrl;
    }

    @Override
    public int connectionPoolMaxSize() {
        return connectionPoolMaxSize;
    }

    @Override
    public int connectionPoolMinIdle() {
        return connectionPoolMinIdle;
    }

    @Override
    public List<ProbeDefinition> probes() {
        return probes;
    }

    private static List<ProbeDefinition> readProbes(Map<String, String>[] probes) {
        // TODO assumes SQLProbe implementation
        return Arrays.stream(probes)
                .map(probe -> new ProbeDefinition(
                        parameterOrBlowUp(probe, "query"),
                        parameterOrBlowUp(probe, "metricName"),
                        parameterOrBlowUp(probe, "metricLabel")
                ))
                .collect(Collectors.toList());
    }

    private static String parameterOrBlowUp(Map<String, String> config, String parameter) {
        if (config == null) {
            throw new IllegalStateException(format("Parent value of field '%s' not present in config.file provided.", parameter));
        }
        String param = config.get(parameter);
        if (param == null) {
            throw new IllegalStateException(format("Expected a value for database field '%s' based on config.file provided.", parameter));
        }
        return param;
    }

    private static int integerParameterOrBlowUp(Map<String, String> config, String parameter) {
        if (config == null) {
            throw new IllegalStateException(format("Parent value of field '%s' not present in config.file provided.", parameter));
        }
        String paramValue = config.get(parameter);
        if (paramValue == null) {
            throw new IllegalStateException(format("Expected a value for field '%s' based on config.file provided.", parameter));
        }
        return Integer.parseInt(paramValue);
    }
}
