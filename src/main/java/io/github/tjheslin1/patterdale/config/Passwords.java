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
package io.github.tjheslin1.patterdale.config;

import io.github.tjheslin1.patterdale.ValueType;

import java.util.Map;

import static java.lang.String.format;

/**
 * The in-memory representation of the 'passwords.yml' file passed in on app start-up.
 */
public class Passwords extends ValueType {

    public Map<String, String> passwords;

    public PasswordDefinition byDatabaseName(String databaseName) {
        if (passwords.containsKey(databaseName)) {
            return new PasswordDefinition(databaseName, passwords.get(databaseName));
        }

        throw new IllegalArgumentException(format("there was no matching database definition for '%s'", databaseName));
    }

}
