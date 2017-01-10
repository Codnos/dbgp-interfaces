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

import com.codnos.dbgp.api.BreakpointUpdateData;
import com.codnos.dbgp.internal.commands.Command;

import static com.codnos.dbgp.internal.commands.breakpoint.BreakpointConverter.breakpointStateAsString;

public class BreakpointUpdateCommand implements Command<BreakpointUpdateResponse> {
    private final String transactionId;
    private final String breakpointId;
    private final BreakpointUpdateData breakpointUpdateData;

    public BreakpointUpdateCommand(String transactionId, String breakpointId, BreakpointUpdateData breakpointUpdateData) {
        this.transactionId = transactionId;
        this.breakpointId = breakpointId;
        this.breakpointUpdateData = breakpointUpdateData;
    }

    @Override
    public String getName() {
        return "breakpoint_update";
    }

    @Override
    public String getMessage() {
        String command = "breakpoint_update -i " + transactionId + " -d " + breakpointId;
        if (breakpointUpdateData.hasState()) {
            command = command + " -s " + breakpointStateAsString(breakpointUpdateData.isEnabled());
        }
        return command;
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + transactionId;
    }

}
