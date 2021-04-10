/*
 * Copyright 2017 - 2021 Thomas Heslin <tjheslin1@kolabnow.com>.
 *
 * This file is part of Patterdale.
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

import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * The in-memory representation of probes list in 'patterdale.yml', passed in on app start-up.
 */
public class Probe extends ValueType {

    public String name;
    public String query;
    public String type;
    public String metricName;
    public String metricLabels;

    // test only
    public static Probe probe(String name, String query, String type, String metricName, String metricLabels) {
        Probe probe = new Probe();
        probe.name = name;
        probe.query = query;
        probe.type = type;
        probe.metricName = metricName;
        probe.metricLabels = metricLabels;

        return probe;
    }

    public Probe dbLabelled(DatabaseDefinition databaseDefinition) {
        return probe(this.name, this.query, this.type, this.metricName, getMetricLabels(databaseDefinition));
    }

    private String getMetricLabels(DatabaseDefinition databaseDefinition) {
        final StringBuilder builder = new StringBuilder("database=\"").append(databaseDefinition.name).append("\"");

        if (databaseDefinition.metricLabels != null) {
            builder.append(',').append(databaseDefinition.metricLabels.entrySet()
                    .stream().map(e -> format("%s=\"%s\"", e.getKey(), e.getValue()))
                    .collect(Collectors.joining(",")));
        }

        if (metricLabels != null && !metricLabels.isEmpty()) {
            builder.append(",").append(this.metricLabels);
        }

        return builder.toString();
    }

    public String query() {
        return query.replaceFirst("\\s*;\\s*$", "");
    }
}
