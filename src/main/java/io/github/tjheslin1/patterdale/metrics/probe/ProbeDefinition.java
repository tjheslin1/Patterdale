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

/**
 * The definition of a SQL probe that will be queried against a database when requested.
 * The metricName and metricLabel define how the result of the query is displayed on the metric webpage.
 */
public class ProbeDefinition extends ValueType {

    public final String sql;
    public final String type;
    public final String metricName;
    public final String metricLabel;

    public ProbeDefinition(String sql, String type, String metricName, String metricLabel) {
        this.sql = sql;
        this.type = type;
        this.metricName = metricName;
        this.metricLabel = metricLabel;
    }
}
