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
import com.codnos.dbgp.internal.messages.CommandResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static com.codnos.dbgp.internal.commands.breakpoint.BreakpointConverter.breakpointFromNode;
import static com.codnos.dbgp.internal.xml.XmlUtil.boolForXPath;

public class BreakpointGetResponse extends CommandResponse {

    public static boolean canBuildFrom(Document document) {
        return boolForXPath(document, "string(/dbgp:response/@command)='breakpoint_get'");
    }

    public BreakpointGetResponse(Document message) {
        super(message);
    }

    public Breakpoint getBreakpoint() {
        NodeList breakpointList = nodeXpath("/dbgp:response/dbgp:breakpoint");
        Node breakpointNode = breakpointList.item(0);
        return breakpointFromNode(breakpointNode);
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + getTransactionId();
    }
}
