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

package com.codnos.dbgp.internal.commands;

import com.codnos.dbgp.api.DebuggerEngine;

import static com.codnos.dbgp.internal.xml.XmlBuilder.e;

public class Init {

    private final DebuggerEngine debuggerEngine;

    public Init(DebuggerEngine debuggerEngine) {
        this.debuggerEngine = debuggerEngine;
    }

    public String asString() {
        return e("init", "urn:debugger_protocol_v1")
                .a("appid", debuggerEngine.getAppId())
                .a("idekey", debuggerEngine.getIdeKey())
                .a("session", debuggerEngine.getSession())
                .a("language", debuggerEngine.getLanguage())
                .a("protocol_version", debuggerEngine.getProtocolVersion())
                .a("fileuri", debuggerEngine.getInitialFileUri())
                .asString();
    }
}
