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

package com.codnos.dbgp.internal.commands.status;

import com.codnos.dbgp.api.DebuggerEngine;
import com.codnos.dbgp.api.Status;
import com.codnos.dbgp.internal.handlers.DBGPCommandHandler;
import io.netty.channel.ChannelHandlerContext;

public final class StatusCommandHandler extends DBGPCommandHandler {

    public StatusCommandHandler(DebuggerEngine debuggerEngine) {
        super(debuggerEngine);
    }

    @Override
    protected boolean canHandle(String msg) {
        return msg.startsWith("status");
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, String msg, DebuggerEngine debuggerEngine) {
        String[] commandParts = msg.split(" ");
        String transactionId = commandParts[2];
        Status status = debuggerEngine.getStatus();
        String responseString = "<response xmlns=\"urn:debugger_protocol_v1\" xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\" command=\"status\"\n" +
                "          transaction_id=\"" + transactionId + "\"\n" +
                "          status=\"" + status.nameForSending() + "\" reason=\"ok\"/>";
        sendBackResponse(ctx, responseString);
    }
}
