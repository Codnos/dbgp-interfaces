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

package com.codnos.dbgp.internal.commands.eval;

import com.codnos.dbgp.api.DebuggerEngine;
import com.codnos.dbgp.api.PropertyValue;
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration;
import com.codnos.dbgp.internal.arguments.Arguments;
import com.codnos.dbgp.internal.handlers.DBGpRegularCommandHandler;
import com.codnos.dbgp.internal.xml.XmlBuilder;

import java.util.Base64;
import java.util.Optional;

import static com.codnos.dbgp.internal.xml.XmlBuilder.e;

public class EvalCommandHandler extends DBGpRegularCommandHandler {

    private final Base64.Decoder base64 = Base64.getDecoder();

    public EvalCommandHandler(DebuggerEngine debuggerEngine, ArgumentConfiguration argumentConfiguration) {
        super(debuggerEngine, argumentConfiguration);
    }

    @Override
    protected boolean canHandle(String msg) {
        return msg.startsWith("eval");
    }

    @Override
    protected String handle(Arguments arguments, DebuggerEngine debuggerEngine) {
        int transactionId = arguments.getInteger("i");
        int depth = 0;
        if (arguments.hasValueFor("d")) {
            depth = arguments.getInteger("d");
        }
        String base64Expression = arguments.getString("-");
        String expression = new String(base64.decode(base64Expression));
        Optional<PropertyValue> optionalPropertyValue = debuggerEngine.eval(depth, expression);
        XmlBuilder response = e("response", "urn:debugger_protocol_v1")
                .a("command", "eval")
                .a("transaction_id", transactionId)
                .a("success", optionalPropertyValue.isPresent() ? "1" : "0");
        if (optionalPropertyValue.isPresent()) {
            PropertyValue propertyValue = optionalPropertyValue.get();
            response.e(e("property")
                    .a("name", propertyValue.getName())
                    .a("fullname", propertyValue.getName())
                    .a("type", propertyValue.getType())
                    .a("encoding", "none")
                    .b(propertyValue.getValue())
            );
        }
        return response.asString();
    }

}
