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

package com.codnos.dbgp.internal.commands.eval;

import com.codnos.dbgp.internal.commands.Command;

import java.util.Base64;

public class EvalCommand implements Command<EvalResponse> {
    private final Base64.Encoder base64 = Base64.getEncoder();
    private final String transactionId;
    private final int depth;
    private final String expression;

    public EvalCommand(String transactionId, int depth, String expression) {
        this.transactionId = transactionId;
        this.depth = depth;
        this.expression = expression;
    }

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public String getMessage() {
        return "eval -i " + transactionId + " -d " + depth + " -- " + base64.encodeToString(expression.getBytes());
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + transactionId;
    }

}
