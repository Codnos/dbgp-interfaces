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

package com.codnos.dbgp.commands.context;

import com.codnos.dbgp.api.DebuggerEngine;
import com.codnos.dbgp.api.PropertyValue;
import com.codnos.dbgp.handlers.DBGPCommandHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;

public class ContextGetCommandHandler extends DBGPCommandHandler {

    public ContextGetCommandHandler(DebuggerEngine debuggerEngine) {
        super(debuggerEngine);
    }

    @Override
    protected boolean canHandle(String msg) {
        return msg.contains("context_get");
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, String msg, DebuggerEngine debuggerEngine) {
        String[] commandParts = msg.split(" ");
        String transactionId = commandParts[2];
        Integer depth = Integer.valueOf(commandParts[4]);
        Collection<PropertyValue> variables = debuggerEngine.getVariables(depth);
        StringBuilder variablesXml = new StringBuilder();
        for (PropertyValue variable : variables) {
            variablesXml.append("<property");
            variablesXml.append(" ");
            variablesXml.append("name=\"");
            variablesXml.append(variable.getName());
            variablesXml.append("\"");
            variablesXml.append(" ");
            variablesXml.append("fullname=\"");
            variablesXml.append(variable.getName());
            variablesXml.append("\"");
            variablesXml.append(" ");
            variablesXml.append("type=\"");
            variablesXml.append(variable.getType());
            variablesXml.append("\"");
            variablesXml.append(" ");
            variablesXml.append("encoding=\"none\"");
            variablesXml.append(">");
            variablesXml.append(variable.getValue());
            variablesXml.append("</property>");
            variablesXml.append("\n");
        }
        String responseString = "<response xmlns=\"urn:debugger_protocol_v1\" xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\" command=\"context_get\"\n" +
                "          transaction_id=\"" + transactionId + "\">" + variablesXml.toString() + "</response>";
        sendBackResponse(ctx, responseString);
    }
}