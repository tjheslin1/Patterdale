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

import io.github.tjheslin1.patterdale.metrics.probe.DatabaseDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * The runtime configuration of the app. Configured by the 'patterdale.yml' file passed in on app start up.
 * This is a flatted representation of {@link PatterdaleConfig}.
 */
public class PatterdaleRuntimeParameters extends ValueType implements RuntimeParameters {

    private final int httpPort;
    private final List<DatabaseDefinition> databases;
    private final int connectionPoolMaxSize;
    private final int connectionPoolMinIdle;

    public PatterdaleRuntimeParameters(int httpPort, List<DatabaseDefinition> databases, int connectionPoolMaxSize, int connectionPoolMinIdle) {
        this.httpPort = httpPort;
        this.databases = databases;
        this.connectionPoolMaxSize = connectionPoolMaxSize;
        this.connectionPoolMinIdle = connectionPoolMinIdle;
    }

    public static PatterdaleRuntimeParameters patterdaleRuntimeParameters(PatterdaleConfig config) {
        return new PatterdaleRuntimeParameters(
                config.httpPort,
                Arrays.asList(config.databases),
                integerParameterOrBlowUp(config.connectionPool, "maxSize"),
                integerParameterOrBlowUp(config.connectionPool, "minIdle")
        );
    }

    @Override
    public int httpPort() {
        return httpPort;
    }

    @Override
    public List<DatabaseDefinition> databases() {
        return databases;
    }

    @Override
    public int connectionPoolMaxSize() {
        return connectionPoolMaxSize;
    }

    @Override
    public int connectionPoolMinIdle() {
        return connectionPoolMinIdle;
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
