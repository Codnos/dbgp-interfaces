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

package com.codnos.dbgp.commands.breakpoint;

import com.codnos.dbgp.api.Breakpoint;
import com.codnos.dbgp.api.DebuggerEngine;
import com.codnos.dbgp.handlers.DBGPCommandHandler;
import io.netty.channel.ChannelHandlerContext;

public class BreakpointSetCommandHandler extends DBGPCommandHandler {

    public BreakpointSetCommandHandler(DebuggerEngine debuggerEngine) {
        super(debuggerEngine);
    }

    @Override
    protected boolean canHandle(String msg) {
        return msg.contains("breakpoint_set");
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, String msg, DebuggerEngine debuggerEngine) {
        String[] commandParts = msg.split(" ");
        String transactionId = commandParts[2];
        String file = commandParts[6];
        String line = commandParts[8];
        String breakPointId = file + "@" + line;
        debuggerEngine.breakpointSet(new Breakpoint(file, Integer.valueOf(line)));
        String responseString = "<response xmlns=\"urn:debugger_protocol_v1\" xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\" command=\"breakpoint_set\"\n" +
                "          transaction_id=\"" + transactionId + "\"\n" +
                "          state=\"enabled\"\n" +
                "          id=\"" + breakPointId + "\"/>";
       sendBackResponse(ctx, responseString);
    }
}
