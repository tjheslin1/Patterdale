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
package io.github.tjheslin1.patterdale.http;

import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class StatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File statusPageContent;
        String statusPageProperty = System.getProperty("status.page");
        if (statusPageProperty != null) {
            statusPageContent = new File(statusPageProperty);
        } else {
            statusPageContent = new File(System.getProperty("config.file"));
        }
        resp.getWriter().println(FileUtils.readFileToString(statusPageContent, UTF_8));
    }
}
