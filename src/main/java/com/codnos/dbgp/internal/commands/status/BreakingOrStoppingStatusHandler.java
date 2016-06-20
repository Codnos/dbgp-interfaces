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

package com.codnos.dbgp.internal.commands.status;

import com.codnos.dbgp.api.Status;
import com.codnos.dbgp.api.StatusChangeHandler;
import com.codnos.dbgp.internal.handlers.ResponseSender;

import java.util.logging.Logger;

import static com.codnos.dbgp.internal.xml.XmlBuilder.e;

public class BreakingOrStoppingStatusHandler implements StatusChangeHandler {
    private static final Logger LOGGER = Logger.getLogger(BreakingOrStoppingStatusHandler.class.getName());
    private final int transactionId;
    private final ResponseSender responseSender;

    public BreakingOrStoppingStatusHandler(int transactionId, ResponseSender responseSender) {
        this.transactionId = transactionId;
        this.responseSender = responseSender;
    }

    @Override
    public void statusChanged(Status previous, Status current) {
        String xml = e("response", "urn:debugger_protocol_v1")
                .a("command", "status")
                .a("transaction_id", transactionId)
                .a("status", current.nameForSending())
                .a("reason", "ok")
                .asString();
        LOGGER.fine("sending message after changed status=" + xml);
        responseSender.send(xml);
    }

    @Override
    public boolean applicableFor(Status previous, Status current) {
        return previous == Status.RUNNING && current == Status.BREAK || previous == Status.STOPPING && current == Status.STOPPED;
    }

}
