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

package com.codnos.dbgp.internal.commands.run;

import com.codnos.dbgp.api.DebuggerEngine;
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration;
import com.codnos.dbgp.internal.arguments.Arguments;
import com.codnos.dbgp.internal.handlers.DBGpRegularCommandHandler;
import com.codnos.dbgp.internal.xml.XmlBuilder;

import static com.codnos.dbgp.internal.xml.XmlBuilder.e;

public class BreakNowCommandHandler extends DBGpRegularCommandHandler {

    public BreakNowCommandHandler(DebuggerEngine debuggerEngine, ArgumentConfiguration argumentConfiguration) {
        super(debuggerEngine, argumentConfiguration);
    }

    @Override
    protected boolean canHandle(String msg) {
        return msg.startsWith("break");
    }

    @Override
    protected String handle(Arguments arguments, DebuggerEngine debuggerEngine) {
        int transactionId = arguments.getInteger("i");
        boolean success = debuggerEngine.breakNow();
        XmlBuilder response = e("response", "urn:debugger_protocol_v1")
                .a("command", "break")
                .a("transaction_id", transactionId)
                .a("success", success ? "1" : "0");
        return response.asString();
    }

}
