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

import io.github.tjheslin1.patterdale.database.DBConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MetricsUseCase {

    private final DBConnectionPool dbConnectionPool;

    public MetricsUseCase(DBConnectionPool dbConnectionPool) {
        this.dbConnectionPool = dbConnectionPool;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public boolean scrapeMetrics() {
        try (Connection connection = dbConnectionPool.pool().connection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1 FROM DUAL")) {
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return false;
            }
            return resultSet.getInt(1) == 1;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
