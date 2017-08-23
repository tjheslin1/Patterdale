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
package io.github.tjheslin1.patterdale.metrics;

import io.github.tjheslin1.patterdale.ValueType;

public class ProbeResult extends ValueType {

    public final boolean result;
    public final String message;
    public final ProbeDefinition probeDefinition;

    ProbeResult(boolean result, String message, ProbeDefinition probeDefinition) {
        this.result = result;
        this.message = message;
        this.probeDefinition = probeDefinition;

    }

    public static ProbeResult success(String message, ProbeDefinition probeDefinition) {
        return new ProbeResult(true, message, probeDefinition);
    }

    public static ProbeResult failure(String message, ProbeDefinition probeDefinition) {
        return new ProbeResult(false, message, probeDefinition);
    }
}
