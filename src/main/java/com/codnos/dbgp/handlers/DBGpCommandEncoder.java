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

import com.codnos.dbgp.Constants;
import com.codnos.dbgp.commands.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class DBGpCommandEncoder extends MessageToByteEncoder {
    private static final int NULL_BYTE = 0;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Command command = (Command) msg;
        String commandMessage = command.getMessage();
        System.out.println("sending command: " + commandMessage);
        out.writeBytes(commandMessage.getBytes(Constants.UTF8));
        out.writeByte(NULL_BYTE);
    }
}
