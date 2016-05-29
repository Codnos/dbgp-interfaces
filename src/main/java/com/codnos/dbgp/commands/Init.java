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

package com.codnos.dbgp.commands;

import com.codnos.dbgp.api.DebuggerEngine;

public class Init {

    private final DebuggerEngine debuggerEngine;

    public Init(DebuggerEngine debuggerEngine) {
        this.debuggerEngine = debuggerEngine;
    }

    public String asString() {
        return "<init xmlns=\"urn:debugger_protocol_v1\" xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\" " +
                "appid=\"" + debuggerEngine.getAppId() + "\" " +
                "idekey=\"" + debuggerEngine.getIdeKey() + "\" " +
                "session=\"" + debuggerEngine.getSession() + "\" " +
                "language=\"" + debuggerEngine.getLanguage() + "\" " +
                "protocol_version=\"" + debuggerEngine.getProtocolVersion() + "\" " +
                "fileuri=\"" + debuggerEngine.getInitialFileUri() + "\"" +
                "/>";
    }
}
