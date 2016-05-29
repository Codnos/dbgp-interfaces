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

package com.codnos.dbgp.commands.stack;

import com.codnos.dbgp.commands.Command;

public class StackDepthCommand implements Command<StackDepthResponse> {
    private final String transactionId;

    public StackDepthCommand(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String getName() {
        return "stack_depth";
    }

    @Override
    public String getMessage() {
        return "stack_depth -i " + transactionId;
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + transactionId;
    }

}
