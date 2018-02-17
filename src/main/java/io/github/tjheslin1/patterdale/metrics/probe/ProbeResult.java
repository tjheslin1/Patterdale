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

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Represents the result of a SQL probes.
 */
public class ProbeResult extends ValueType {

    public final double value;
    public final Probe probe;
    public final List<String> dynamicLabelValues;

    public ProbeResult(double value, Probe probe) {
        this(value, probe, emptyList());
    }

    public ProbeResult(double value, Probe probe, List<String> dynamicLabelValues) {
        this.value = value;
        this.probe = probe;
        this.dynamicLabelValues = dynamicLabelValues;
    }

    public static ProbeResult failedProbe(Probe probe) {
        return new ProbeResult(-1, probe);
    }
}
