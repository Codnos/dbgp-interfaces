/*
 * Copyright 2016 Codnos Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codnos.dbgp.api;

import com.codnos.dbgp.internal.impl.DBGpEngineImpl;
import com.codnos.dbgp.internal.impl.DBGpIdeImpl;
import com.codnos.dbgp.internal.impl.StatusChangeHandlerFactory;

public class DBGpFactory {
    public static DBGpIdeBuilder ide() {
        return new DBGpIdeBuilder();
    }

    public static DBGpEngineBuilder engine() {
        return new DBGpEngineBuilder();
    }

    private static class DBGpIdeBuilder {
        private int port;
        private DebuggerIde debuggerIde;

        public DBGpIdeBuilder withPort(int port) {
            this.port = port;
            return this;
        }

        public DBGpIdeBuilder withDebuggerIde(DebuggerIde debuggerIde) {
            this.debuggerIde = debuggerIde;
            return this;
        }

        public DBGpIde build() {
            validate("port", port);
            validate("debuggerIde", debuggerIde);
            return new DBGpIdeImpl(port, debuggerIde);
        }
    }

    private static class DBGpEngineBuilder {
        private int port;
        private DebuggerEngine debuggerEngine;
        private StatusChangeHandlerFactory statusChangeHandlerFactory = new StatusChangeHandlerFactory();

        public DBGpEngineBuilder withPort(int port) {
            this.port = port;
            return this;
        }

        public DBGpEngineBuilder withDebuggerEngine(DebuggerEngine debuggerEngine) {
            this.debuggerEngine = debuggerEngine;
            return this;
        }

        DBGpEngineBuilder withStatusChangeHandlerFactory(StatusChangeHandlerFactory statusChangeHandlerFactory) {
            this.statusChangeHandlerFactory = statusChangeHandlerFactory;
            return this;
        }

        public DBGpEngine build() {
            validate("port", port);
            validate("debuggerEngine", debuggerEngine);
            validate("statusChangeHandlerFactory", statusChangeHandlerFactory);
            return new DBGpEngineImpl(port, debuggerEngine, statusChangeHandlerFactory);
        }
    }

    private static void validate(String field, Object value) {
        if (value == null) {
            throw new IllegalStateException("Field " + field + " was not set but is required!");
        }
    }
}
