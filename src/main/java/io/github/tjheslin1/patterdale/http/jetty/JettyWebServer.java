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
package io.github.tjheslin1.patterdale.http.jetty;

import io.github.tjheslin1.patterdale.http.WebServer;
import org.eclipse.jetty.server.Server;

public class JettyWebServer implements WebServer {

    private final Server server;

    public JettyWebServer(Server server) {
        this.server = server;
    }


    @Override
    public void start() throws Exception {
        server.start();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

    @Override
    public String baseUrl() {
        return server.getURI().toString();
    }
}
