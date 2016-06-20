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

package com.codnos.dbgp.internal.commands.stack;

import com.codnos.dbgp.api.DebuggerEngine;
import com.codnos.dbgp.api.StackFrame;
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration;
import com.codnos.dbgp.internal.arguments.Arguments;
import com.codnos.dbgp.internal.handlers.DBGpCommandHandler;
import com.codnos.dbgp.internal.xml.XmlBuilder;
import io.netty.channel.ChannelHandlerContext;

import static com.codnos.dbgp.internal.xml.XmlBuilder.e;

public class StackGetCommandHandler extends DBGpCommandHandler {

    public StackGetCommandHandler(DebuggerEngine debuggerEngine, ArgumentConfiguration argumentConfiguration) {
        super(debuggerEngine, argumentConfiguration);
    }

    @Override
    protected boolean canHandle(String msg) {
        return msg.startsWith("stack_get");
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, Arguments arguments, DebuggerEngine debuggerEngine) {
        int transactionId = arguments.getInteger("i");
        Integer depth = arguments.getInteger("d");
        StackFrame frame = debuggerEngine.getFrame(depth);
        XmlBuilder stack = e("stack")
                .a("level", depth)
                .a("type", "file")
                .a("filename", frame.getFileURL())
                .a("lineno", frame.getLineNumber());
        if (frame.getWhere() != null) {
            stack.a("where", frame.getWhere());
        }
        String xml = e("response", "urn:debugger_protocol_v1")
                .a("command", "stack_get")
                .a("transaction_id", transactionId)
                .e(stack)
                .asString();
        sendBackResponse(ctx, xml);
    }
}
