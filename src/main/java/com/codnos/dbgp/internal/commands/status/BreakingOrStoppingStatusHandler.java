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

package com.codnos.dbgp.internal.commands.status;

import com.codnos.dbgp.api.Status;
import com.codnos.dbgp.api.StatusChangeHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Logger;

public class BreakingOrStoppingStatusHandler implements StatusChangeHandler {
    private static final Logger LOGGER = Logger.getLogger(BreakingOrStoppingStatusHandler.class.getName());
    private final String transactionId;
    private final ChannelHandlerContext ctx;

    public BreakingOrStoppingStatusHandler(String transactionId, ChannelHandlerContext ctx) {
        this.transactionId = transactionId;
        this.ctx = ctx;
    }

    @Override
    public void statusChanged(Status previous, Status current) {
        String message = "<response xmlns=\"urn:debugger_protocol_v1\" xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\"  command=\"status\"\n" +
                "          status=\"" + current.nameForSending() + "\"\n" +
                "          reason=\"ok\"\n" +
                "          transaction_id=\"" + transactionId + "\">\n" +
                "</response>";
        LOGGER.fine("sending message after changed status=" + message);
        LOGGER.fine("in ctx = " + ctx);
        ctx.writeAndFlush(message);
    }

    @Override
    public boolean applicableFor(Status previous, Status current) {
        return previous == Status.RUNNING && current == Status.BREAK || previous == Status.STOPPING && current == Status.STOPPED;
    }

}
