/*
 * Copyright 2019 Thomas Heslin <tjheslin1@kolabnow.com>.
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
package io.github.tjheslin1.patterdale.database.hikari;

import com.zaxxer.hikari.HikariDataSource;
import io.github.tjheslin1.patterdale.database.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariDBConnection implements DBConnection {

    private final HikariDataSource hikariDataSource;

    public HikariDBConnection(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public Connection connection() throws SQLException {
        return hikariDataSource.getConnection();
    }
}
