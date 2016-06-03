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

package com.codnos.dbgp.internal.commands.context;

import com.codnos.dbgp.api.PropertyValue;
import com.codnos.dbgp.internal.messages.CommandResponse;
import com.codnos.dbgp.internal.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;

public class ContextGetResponse extends CommandResponse {
    public static boolean canBuildFrom(Document document) {
        return XmlUtil.boolForXPath(document, "string(/dbgp:response/@command)='context_get'");
    }

    public ContextGetResponse(Document message) {
        super(message);
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + getTransactionId();
    }

    public Collection<PropertyValue> getVariables() {
        ArrayList<PropertyValue> propertyValues = new ArrayList<PropertyValue>();
        NodeList nodeList = nodeXpath("/dbgp:response/dbgp:property");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String name = XmlUtil.stringForXPath(node, "@name");
            String type = XmlUtil.stringForXPath(node, "@type");
            String value = XmlUtil.stringForXPath(node, "text()");
            PropertyValue propertyValue = new PropertyValue(name, type, value);
            propertyValues.add(propertyValue);
        }
        return propertyValues;
    }
}
