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

import com.google.common.collect.ImmutableMap;
import io.github.tjheslin1.patterdale.database.DBConnectionPool;
import org.slf4j.Logger;

import java.util.Map;

import static java.lang.String.format;

// TODO potentially rename
public class TypeToProbeMapper {

    private static final String EXISTS = "exists";

    private static final Map<String, Class> typeToProbe = ImmutableMap.of(
            EXISTS, ExistsOracleSQLProbe.class
    );

    public OracleSQLProbe createProbe(ProbeDefinition probeDefinition, DBConnectionPool dbConnectionPool, Logger logger) {
        Class probeClass = typeToProbe.get(probeDefinition.type.toLowerCase().trim());

        if (probeClass == ExistsOracleSQLProbe.class) {
            return new ExistsOracleSQLProbe(probeDefinition, dbConnectionPool, logger);
        } else {
            throw new IllegalArgumentException(
                    format("Expected the provided 'type' value '%s' to match an OracleSqlProbe definition.", probeDefinition.type));
        }
    }
}
