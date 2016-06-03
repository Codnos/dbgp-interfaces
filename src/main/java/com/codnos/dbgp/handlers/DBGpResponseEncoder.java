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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DBGpResponseEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf byteBuf) throws Exception {
        String message = (String) msg;
        byte[] initBytes = message.getBytes(UTF_8);
        String size = String.valueOf(initBytes.length);
        byte[] sizeBytes = size.getBytes(UTF_8);
        final ByteBuf initMessageBuffer = ctx.alloc().buffer(sizeBytes.length + 1 + initBytes.length + 1);
        initMessageBuffer.writeBytes(sizeBytes);
        initMessageBuffer.writeZero(1);
        initMessageBuffer.writeBytes(initBytes);
        initMessageBuffer.writeZero(1);
        System.out.println("sending response=" + initMessageBuffer.toString(UTF_8));
        ctx.writeAndFlush(initMessageBuffer);
    }
}
