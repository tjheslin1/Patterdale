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
package io.github.tjheslin1.patterdale.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;

/**
 * Parses the provided 'passwords.yml' file into an in-memory representation.
 */
public class PasswordsUnmarshaller {

    private final Logger logger;

    public PasswordsUnmarshaller(Logger logger) {
        this.logger = logger;
    }

    /**
     * Unmarshalls the provided 'passwords.yml' file into an in-memory representation.
     *
     * @param passwordFile the 'passwords.yml' file to be unmarshalled.
     * @return an in memory representation of the config.
     */
    public Passwords parsePasswords(File passwordFile) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(passwordFile, Passwords.class);
        } catch (IOException e) {
            logger.error("Failed to parse provided system property 'passwords.file'.", e);
            throw new IllegalArgumentException(format("Error occurred reading passwords file '%s'", passwordFile.getName()), e);
        }
    }
}
