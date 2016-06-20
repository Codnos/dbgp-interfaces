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

package com.codnos.dbgp.internal.commands.run;

import com.codnos.dbgp.api.DebuggerEngine;
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration;
import com.codnos.dbgp.internal.arguments.Arguments;
import com.codnos.dbgp.internal.handlers.DBGpContinuationCommandHandler;
import com.codnos.dbgp.internal.handlers.ResponseSender;
import com.codnos.dbgp.internal.impl.StatusChangeHandlerFactory;

public final class RunCommandHandler extends DBGpContinuationCommandHandler {

    private final StatusChangeHandlerFactory statusChangeHandlerFactory;

    public RunCommandHandler(DebuggerEngine debuggerEngine, StatusChangeHandlerFactory statusChangeHandlerFactory, ArgumentConfiguration argumentConfiguration) {
        super(debuggerEngine, argumentConfiguration);
        this.statusChangeHandlerFactory = statusChangeHandlerFactory;
    }

    @Override
    protected boolean canHandle(String msg) {
        return msg.startsWith("run");
    }

    @Override
    protected void handle(Arguments arguments, DebuggerEngine debuggerEngine, ResponseSender responseSender) throws Exception {
        int transactionId = arguments.getInteger("i");
        debuggerEngine.registerStatusChangeHandler(statusChangeHandlerFactory.getInstance(transactionId, responseSender));
        debuggerEngine.run();
    }
}
