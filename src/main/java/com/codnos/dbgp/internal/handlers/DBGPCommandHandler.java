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

package com.codnos.dbgp.internal.handlers;

import com.codnos.dbgp.api.DebuggerEngine;
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration;
import com.codnos.dbgp.internal.arguments.Arguments;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

public abstract class DBGpCommandHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = Logger.getLogger(DBGpCommandHandler.class.getName());

    private final DebuggerEngine debuggerEngine;
    private final ArgumentConfiguration argumentConfiguration;

    public DBGpCommandHandler(DebuggerEngine debuggerEngine, ArgumentConfiguration argumentConfiguration) {
        this.debuggerEngine = debuggerEngine;
        this.argumentConfiguration = argumentConfiguration;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String in = (String) msg;
        LOGGER.fine("got message=" + in);
        LOGGER.fine("Checking if " + this.getClass().getCanonicalName() + " class can handle " + msg);
        boolean canHandle = canHandle(in);
        LOGGER.fine("After checking we the result was:" + canHandle);
        if (canHandle) {
            Arguments arguments = argumentConfiguration.buildArgumentsFrom(in);
            handle(ctx, arguments, debuggerEngine);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void sendBackResponse(ChannelHandlerContext ctx, String responseString) {
        ChannelFuture channelFuture = ctx.writeAndFlush(responseString);
        try {
            ChannelFuture sync = channelFuture.sync();
            LOGGER.fine("isdone sending = " + sync.isDone());
            LOGGER.fine("was success= " + sync.isSuccess());
        } catch (InterruptedException e) {
            LOGGER.fine("got interrupted");
            e.printStackTrace();
        }
    }

    protected abstract boolean canHandle(String msg);

    protected abstract void handle(ChannelHandlerContext ctx, Arguments arguments, DebuggerEngine debuggerEngine) throws Exception;
}
