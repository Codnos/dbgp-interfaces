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

package com.codnos.dbgp.commands.stack;

import com.codnos.dbgp.api.DebuggerEngine;
import com.codnos.dbgp.handlers.DBGPCommandHandler;
import io.netty.channel.ChannelHandlerContext;

public class StackDepthCommandHandler extends DBGPCommandHandler {

    public StackDepthCommandHandler(DebuggerEngine debuggerEngine) {
        super(debuggerEngine);
    }

    @Override
    protected boolean canHandle(String msg) {
        return msg.contains("stack_depth");
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, String msg, DebuggerEngine debuggerEngine) {
        String[] commandParts = msg.split(" ");
        String transactionId = commandParts[2];
        Integer depth = debuggerEngine.getStackDepth();
        String responseString = "<response xmlns=\"urn:debugger_protocol_v1\" xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\" command=\"stack_depth\"\n" +
                "          transaction_id=\"" + transactionId + "\"\n" +
                "          depth=\"" + depth + "\"/>";
        sendBackResponse(ctx, responseString);
    }
}