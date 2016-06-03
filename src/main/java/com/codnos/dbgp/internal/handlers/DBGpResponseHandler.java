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

import com.codnos.dbgp.internal.messages.MessageFactory;
import com.codnos.dbgp.internal.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DBGpResponseHandler extends ChannelInboundHandlerAdapter {
    private final DBGpEventsHandler eventsHandler;

    public DBGpResponseHandler(DBGpEventsHandler eventsHandler) {
        this.eventsHandler = eventsHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = (String) msg;
        System.out.println("message received=" + message);
        Message responseMessage = MessageFactory.getMessage(message);
        eventsHandler.receive(responseMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
