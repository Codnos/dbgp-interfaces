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

import com.codnos.dbgp.messages.CommandResponse;
import com.codnos.dbgp.xml.XmlUtil;
import org.w3c.dom.Document;

public class StackGetResponse extends CommandResponse {
    public static boolean canBuildFrom(Document document) {
        return XmlUtil.boolForXPath(document, "string(/dbgp:response/@command)='stack_get'");
    }

    public StackGetResponse(Document message) {
        super(message);
    }

    public String getFileUrl() {
        return xpath("/dbgp:response/dbgp:stack/@filename");
    }

    public Integer getLineNumber() {
        return intXpath("/dbgp:response/dbgp:stack/@lineno");
    }

    public String getWhere() {
        return xpath("/dbgp:response/dbgp:stack/@where");
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + getTransactionId();
    }
}
