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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;

import java.util.List;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DBGpResponseDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = Logger.getLogger(DBGpResponseDecoder.class.getName());

    private static final int NULL_BYTE_SIZE = 1;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> objects) throws Exception {
        final int length = in.readableBytes();
        LOGGER.fine("got something from engine (" + length + " bytes)");
        int nullPosition = in.forEachByte(ByteProcessor.FIND_NUL);
        int readerIndex = in.readerIndex();
        int numberOfBytes = nullPosition - readerIndex;
        LOGGER.fine("found nullposition on " + nullPosition + " and readerIndex is " + readerIndex + " calculated number of bytes " + numberOfBytes);
        if (numberOfBytes <= 0) {
            LOGGER.fine("not enough to read, finishing");
            in.resetReaderIndex();
            return;
        }
        if (nullPosition > length) {
            LOGGER.fine("have null position further than length, finishing");
            in.resetReaderIndex();
            return;
        }
        ByteBuf sizeBuf = in.readBytes(numberOfBytes);
        in.readByte();
        String sizeBufAsString = sizeBuf.toString(UTF_8);
        int size = Integer.parseInt(sizeBufAsString);
        int expectedSize = sizeBuf.readableBytes() + NULL_BYTE_SIZE + size + NULL_BYTE_SIZE;
        if (length < expectedSize) {
            LOGGER.fine("don't have the whole message yet (expected " + expectedSize + "), finishing");
            in.resetReaderIndex();
            sizeBuf.release();
            return;
        }
        ByteBuf messageBuf = in.readBytes(size);
        in.readByte();
        objects.add(messageBuf.toString(UTF_8));
        sizeBuf.release();
        messageBuf.release();
    }
}
