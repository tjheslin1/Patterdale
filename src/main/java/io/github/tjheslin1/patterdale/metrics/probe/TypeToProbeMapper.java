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
package io.github.tjheslin1.patterdale.metrics.probe;

import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import org.slf4j.Logger;

import static java.lang.String.format;

public class TypeToProbeMapper {

    private static final String EXISTS = "exists";

    private final DBConnectionPool dbConnectionPool;
    private final Logger logger;

    public TypeToProbeMapper(DBConnectionPool dbConnectionPool, Logger logger) {
        this.dbConnectionPool = dbConnectionPool;
        this.logger = logger;
    }

    public OracleSQLProbe createProbe(ProbeDefinition probeDefinition) {
        switch (probeDefinition.type) {
            case EXISTS: {
                return new ExistsOracleSQLProbe(probeDefinition, dbConnectionPool, logger);
            }
            default: {
                throw new IllegalArgumentException(
                        format("Expected the provided 'type' value '%s' to match an OracleSqlProbe definition.", probeDefinition.type));
            }
        }
    }
}
