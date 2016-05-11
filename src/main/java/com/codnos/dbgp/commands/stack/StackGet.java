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

import com.codnos.dbgp.DebuggerEngine;
import com.codnos.dbgp.commands.Command;
import com.codnos.dbgp.handlers.DBGPCommandHandler;
import com.codnos.dbgp.messages.CommandResponse;
import com.codnos.dbgp.xml.XmlUtil;
import io.netty.channel.ChannelHandlerContext;
import org.w3c.dom.Document;

public class StackGet implements Command<StackGet.Response> {
    private final String transactionId;
    private final int depth;

    public StackGet(String transactionId, int depth) {
        this.transactionId = transactionId;
        this.depth = depth;
    }

    @Override
    public String getName() {
        return "stack_get";
    }

    @Override
    public String getMessage() {
        return "stack_get -i " + transactionId + " -d " + depth;
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + transactionId;
    }

    public static class Response extends CommandResponse {
        public static boolean canBuildFrom(Document document) {
            return XmlUtil.boolForXPath(document, "string(/dbgp:response/@command)='stack_get'");
        }

        public Response(Document message) {
            super(message);
        }

        public String getFileUrl() {
            return xpath("/dbgp:response/dbgp:stack/@filename");
        }

        public Integer getLineNumber() {
            return intXpath("/dbgp:response/dbgp:stack/@lineno");
        }

        public String getWhere() {
            return xpath("/dbgp:response/dbgp:stack/@where");
        }

        @Override
        public String getHandlerKey() {
            return getName() + ":" + getTransactionId();
        }
    }

    public static class CommandHandler extends DBGPCommandHandler {

        public CommandHandler(DebuggerEngine debuggerEngine) {
            super(debuggerEngine);
        }

        @Override
        protected boolean canHandle(String msg) {
            return msg.contains("stack_get");
        }

        @Override
        protected void handle(ChannelHandlerContext ctx, String msg, DebuggerEngine debuggerEngine) {
            String[] commandParts = msg.split(" ");
            String transactionId = commandParts[2];
            Integer depth = Integer.valueOf(commandParts[4]);
            StackFrame frame = debuggerEngine.getFrame(depth);
            String responseString = "<response xmlns=\"urn:debugger_protocol_v1\" xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\" command=\"stack_get\"\n" +
                    "          transaction_id=\"" + transactionId + "\">" +
                    "<stack level=\""+depth+"\" type=\"file\" filename=\""+ frame.getFileURL() + "\" lineno=\"" + frame.getLineNumber() + "\"" +
                    (frame.getWhere() != null ? " where=\"" + frame.getWhere() +"\"" : "")
                    + "/>" +
                    "</response>";
            sendBackResponse(ctx, responseString);
        }
    }
}