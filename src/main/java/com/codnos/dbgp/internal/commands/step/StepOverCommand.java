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

package com.codnos.dbgp.internal.commands.step;

import com.codnos.dbgp.internal.commands.ContinuationCommand;

public class StepOverCommand implements ContinuationCommand<Void> {
    private final String transactionId;

    public StepOverCommand(String transactionId) {
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

}
