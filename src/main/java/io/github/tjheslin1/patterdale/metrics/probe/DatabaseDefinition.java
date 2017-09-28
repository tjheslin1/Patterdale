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

import io.github.tjheslin1.patterdale.ValueType;

import java.util.List;

/**
 * The in-memory representation of databases list in 'patterdale.yml', passed in on app start-up.
 */
public class DatabaseDefinition extends ValueType {
    public String name;
    public String user;
    public String jdbcUrl;
    public String[] probes;

    // test use only
    public static DatabaseDefinition databaseDefinition(String name, String user, String jdbcUrl, List<String> probes) {
        DatabaseDefinition databaseDefinition = new DatabaseDefinition();
        databaseDefinition.name = name;
        databaseDefinition.user = user;
        databaseDefinition.jdbcUrl = jdbcUrl;
        databaseDefinition.probes = probes.toArray(new String[probes.size()]);

        return databaseDefinition;
    }
}
