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

package com.codnos.dbgp.commands.breakpoint;

import com.codnos.dbgp.api.Breakpoint;
import com.codnos.dbgp.api.DebuggerEngine;
import com.codnos.dbgp.commands.Command;
import com.codnos.dbgp.handlers.DBGPCommandHandler;
import io.netty.channel.ChannelHandlerContext;

public class BreakpointSetCommand implements Command<BreakpointSetResponse> {
    private final String transactionId;
    private final Breakpoint breakpoint;

    public BreakpointSetCommand(String transactionId, Breakpoint breakpoint) {
        this.transactionId = transactionId;
        this.breakpoint = breakpoint;
    }

    @Override
    public String getName() {
        return "breakpoint_set";
    }

    @Override
    public String getMessage() {
        return "breakpoint_set -i " + transactionId + " -t line -f " + breakpoint.getFileURL() + " -n " + breakpoint.getLineNumber();
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + transactionId;
    }

}