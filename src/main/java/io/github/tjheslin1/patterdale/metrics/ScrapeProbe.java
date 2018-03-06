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
package io.github.tjheslin1.patterdale.metrics;

import io.github.tjheslin1.patterdale.metrics.probe.OracleSQLProbe;
import io.github.tjheslin1.patterdale.metrics.probe.ProbeResult;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public class ScrapeProbe implements Callable<List<ProbeResult>> {
    private final OracleSQLProbe probe;

    public ScrapeProbe(OracleSQLProbe probe) {
        this.probe = probe;
    }

    @Override
    public List<ProbeResult> call() {
        return probe.probes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScrapeProbe that = (ScrapeProbe) o;
        return Objects.equals(probe, that.probe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(probe);
    }
}