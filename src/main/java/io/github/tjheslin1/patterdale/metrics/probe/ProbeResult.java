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
 * Represents the result of a SQL probe.
 */
public class ProbeResult extends ValueType {

    public final boolean result;
    public final String message;
    public final Probe probe;

    public ProbeResult(boolean result, String message, Probe probe) {
        this.result = result;
        this.message = message;
        this.probe = probe;

    }

    public static ProbeResult success(String message, Probe probe) {
        return new ProbeResult(true, message, probe);
    }

    public static ProbeResult failure(String message, Probe probe) {
        return new ProbeResult(false, message, probe);
    }
}
