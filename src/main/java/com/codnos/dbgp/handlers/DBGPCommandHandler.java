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

package com.codnos.dbgp.handlers;

import com.codnos.dbgp.api.DebuggerEngine;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class DBGPCommandHandler extends ChannelInboundHandlerAdapter {

    private final DebuggerEngine debuggerEngine;

    public DBGPCommandHandler(DebuggerEngine debuggerEngine) {
        this.debuggerEngine = debuggerEngine;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String in = (String) msg;
        System.out.println("got message=" + in);
        System.out.println("Checking if " +this.getClass().getCanonicalName() + " class can handle " + msg);
        boolean canHandle = canHandle(in);
        System.out.println("After checking we the result was:" + canHandle);
        if (canHandle) {
            handle(ctx, in, debuggerEngine);
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
            System.out.println("isdone sending = "+ sync.isDone());
            System.out.println("was success= "+ sync.isSuccess());
        } catch (InterruptedException e) {
            System.out.println("got interrupted");
            e.printStackTrace();
        }
    }

    protected abstract boolean canHandle(String msg);

    protected abstract void handle(ChannelHandlerContext ctx, String msg, DebuggerEngine debuggerEngine) throws Exception;
}
