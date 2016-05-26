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

package com.codnos.dbgp.commands.status;

import io.netty.channel.ChannelHandlerContext;

public class BreakingOrStoppingStateHandler implements StateChangeHandler {
    private final String transactionId;
    private final ChannelHandlerContext ctx;

    public BreakingOrStoppingStateHandler(String transactionId, ChannelHandlerContext ctx) {
        this.transactionId = transactionId;
        this.ctx = ctx;
    }

    @Override
    public void stateChanged(State previous, State current) {
        String message = "<response xmlns=\"urn:debugger_protocol_v1\" xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\"  command=\"status\"\n" +
                "          status=\"" + current.nameForSending() + "\"\n" +
                "          reason=\"ok\"\n" +
                "          transaction_id=\"" + transactionId + "\">\n" +
                "</response>";
        System.out.println("sending message after changed state=" + message);
        System.out.println("in ctx = " + ctx);
        ctx.writeAndFlush(message);
    }

    @Override
    public boolean applicableFor(State previous, State current) {
        return previous == State.RUNNING && current == State.BREAK || previous == State.STOPPING && current == State.STOPPED;
    }

}
