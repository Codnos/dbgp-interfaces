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

package com.codnos.dbgp.commands.step;

import com.codnos.dbgp.DebuggerEngine;
import com.codnos.dbgp.commands.ContinuationCommand;
import com.codnos.dbgp.commands.BreakingOrStoppingStateHandler;
import com.codnos.dbgp.handlers.DBGPCommandHandler;
import io.netty.channel.ChannelHandlerContext;

public class StepOver implements ContinuationCommand<Void> {
    private final String transactionId;

    public StepOver(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String getName() {
        return "step_over";
    }

    @Override
    public String getMessage() {
        return getName() + " -i " + transactionId;
    }

    @Override
    public String getHandlerKey() {
        return "status:" + transactionId;
    }

    public static final class CommandHandler extends DBGPCommandHandler {

        public CommandHandler(DebuggerEngine debuggerEngine) {
            super(debuggerEngine);
        }

        @Override
        protected boolean canHandle(String msg) {
            return msg.contains("step_over");
        }

        @Override
        protected void handle(final ChannelHandlerContext ctx, String msg, DebuggerEngine debuggerEngine) throws Exception {
            String[] commandParts = msg.split(" ");
            final String transactionId = commandParts[2];
            debuggerEngine.registerStateChangeHandler(new BreakingOrStoppingStateHandler(transactionId, ctx));
            debuggerEngine.stepOver();
        }
    }
}
