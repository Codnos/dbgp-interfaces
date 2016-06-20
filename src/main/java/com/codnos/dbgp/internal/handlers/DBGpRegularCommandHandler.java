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

import java.util.logging.Logger;

public abstract class DBGpRegularCommandHandler extends DBGpCommandHandler {

    private static final Logger LOGGER = Logger.getLogger(DBGpRegularCommandHandler.class.getName());

    public DBGpRegularCommandHandler(DebuggerEngine debuggerEngine, ArgumentConfiguration argumentConfiguration) {
        super(debuggerEngine, argumentConfiguration);
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, Arguments arguments, DebuggerEngine debuggerEngine) throws Exception {
        String response = handle(arguments, debuggerEngine);
        sendBackResponse(ctx, response);
    }

    protected abstract String handle(Arguments arguments, DebuggerEngine debuggerEngine);


    private void sendBackResponse(ChannelHandlerContext ctx, String responseString) {
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
}
