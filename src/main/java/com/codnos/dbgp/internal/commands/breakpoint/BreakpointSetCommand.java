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

package com.codnos.dbgp.internal.commands.breakpoint;

import com.codnos.dbgp.api.Breakpoint;
import com.codnos.dbgp.internal.commands.Command;

import java.util.Base64;

public class BreakpointSetCommand implements Command<BreakpointSetResponse> {
    private final Base64.Encoder base64 = Base64.getEncoder();
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
        StringBuilder command = new StringBuilder();
        command
                .append("breakpoint_set -i ").append(transactionId)
                .append(" -t ").append(breakpoint.getType().asString())
                .append(" -f ").append(breakpoint.getFileURL().get())
                .append(" -n ").append(breakpoint.getLineNumber().get());
        if (breakpoint.isTemporary()) {
            command.append(" -r 1");
        }
        if (breakpoint.getExpression().isPresent()) {
            String plainExpression = breakpoint.getExpression().get();
            command.append(" -- ").append(base64.encodeToString(plainExpression.getBytes()));
        }
        return command.toString();
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + transactionId;
    }

}
