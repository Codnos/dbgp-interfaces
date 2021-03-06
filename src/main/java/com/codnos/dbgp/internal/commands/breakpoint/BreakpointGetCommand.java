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

import com.codnos.dbgp.internal.commands.Command;

public class BreakpointGetCommand implements Command<BreakpointGetResponse> {
    private final String transactionId;
    private final String breakpointId;

    public BreakpointGetCommand(String transactionId, String breakpointId) {
        this.transactionId = transactionId;
        this.breakpointId = breakpointId;
    }

    @Override
    public String getName() {
        return "breakpoint_get";
    }

    @Override
    public String getMessage() {
        return "breakpoint_get -i " + transactionId + " -d " + breakpointId;
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + transactionId;
    }

}
