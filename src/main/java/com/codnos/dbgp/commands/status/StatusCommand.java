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

package com.codnos.dbgp.commands.status;

import com.codnos.dbgp.api.DebuggerEngine;
import com.codnos.dbgp.api.State;
import com.codnos.dbgp.commands.Command;
import com.codnos.dbgp.handlers.DBGPCommandHandler;
import com.codnos.dbgp.messages.CommandResponse;
import com.codnos.dbgp.xml.XmlUtil;
import io.netty.channel.ChannelHandlerContext;
import org.w3c.dom.Document;

public class StatusCommand implements Command<StatusCommand.StatusCommandResponse> {

    private String transactionId;

    public StatusCommand(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getMessage() {
        return "status -i " + transactionId;
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + transactionId;
    }

    public static final class StatusCommandResponse extends CommandResponse {

        public static boolean canBuildFrom(Document document) {
            return XmlUtil.boolForXPath(document, "string(/dbgp:response/@command)='status'");
        }

        public StatusCommandResponse(Document message) {
            super(message);
        }

        @Override
        public String getHandlerKey() {
            return getName() + ":" + getTransactionId();
        }

        public State getStatus() {
            return State.fromSentName(xpath("/dbgp:response/@status"));
        }
    }

    public static final class StatusCommandHandler extends DBGPCommandHandler {

        public StatusCommandHandler(DebuggerEngine debuggerEngine) {
            super(debuggerEngine);
        }

        @Override
        protected boolean canHandle(String msg) {
            return msg.contains("status");
        }

        @Override
        protected void handle(ChannelHandlerContext ctx, String msg, DebuggerEngine debuggerEngine) {
            String[] commandParts = msg.split(" ");
            String transactionId = commandParts[2];
            State state = debuggerEngine.getState();
            String responseString = "<response xmlns=\"urn:debugger_protocol_v1\" xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\" command=\"status\"\n" +
                    "          transaction_id=\"" + transactionId + "\"\n" +
                    "          status=\"" + state.nameForSending() + "\" reason=\"ok\"/>";
            sendBackResponse(ctx, responseString);
        }
    }
}
