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

import java.util.Map;

import static java.lang.String.format;

public class PatterdaleRuntimeParameters extends ValueType implements RuntimeParameters {

    private final int httpPort;
    private final String databaseServerName;
    private final String databaseName;
    private final String databaseNetworkProtocol;
    private final String driverType;
    private final String jdbcUrl;
    private final int connectionPoolMaxSize;
    private final int connectionPoolMinIdle;

    PatterdaleRuntimeParameters(int httpPort, String serverName, String databaseName, String protocol, String driverType, String jdbcUrl, int connectionPoolMaxSize, int connectionPoolMinIdle) {
        this.httpPort = httpPort;
        this.databaseServerName = serverName;
        this.databaseName = databaseName;
        this.databaseNetworkProtocol = protocol;
        this.driverType = driverType;
        this.jdbcUrl = jdbcUrl;
        this.connectionPoolMaxSize = connectionPoolMaxSize;
        this.connectionPoolMinIdle = connectionPoolMinIdle;
    }

    public static PatterdaleRuntimeParameters patterdaleRuntimeParameters(PatterdaleConfig config) {
        return new PatterdaleRuntimeParameters(
                config.httpPort,
                parameterOrBlowUp(config.database, "serverName"),
                parameterOrBlowUp(config.database, "name"),
                parameterOrBlowUp(config.database, "networkProtocol"),
                parameterOrBlowUp(config.database, "driverType"),
                parameterOrBlowUp(config.database, "jdbcUrl"),
                integerParameterOrBlowUp(config.connectionPool, "maxSize"),
                integerParameterOrBlowUp(config.connectionPool, "minIdle"));
    }

    @Override
    public int httpPort() {
        return httpPort;
    }

    @Override
    public String databaseServerName() {
        return databaseServerName;
    }

    @Override
    public String databaseName() {
        return databaseName;
    }

    @Override
    public String databaseNetworkProtocol() {
        return databaseNetworkProtocol;
    }

    @Override
    public String driverType() {
        return driverType;
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

    private static String parameterOrBlowUp(Map<String, String> config, String parameter) {
        String param = config.get(parameter);
        if (param == null) {
            throw new IllegalStateException(format("Expected a value for database field '%s' based on config.file provided.", parameter));
        }
        return param;
    }

    private static int integerParameterOrBlowUp(Map<String, String> config, String parameter) {
        String paramValue = config.get(parameter);
        if (paramValue == null) {
            throw new IllegalStateException(format("Expected a value for field '%s' based on config.file provided.", parameter));
        }
        int param = Integer.parseInt(paramValue);
        return param;
    }
}
