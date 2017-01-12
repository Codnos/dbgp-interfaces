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

import com.codnos.dbgp.api.PropertyValue;
import com.codnos.dbgp.internal.messages.CommandResponse;
import com.codnos.dbgp.internal.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;

import static com.codnos.dbgp.internal.xml.XmlUtil.boolForXPath;

public class EvalResponse extends CommandResponse {

    public static boolean canBuildFrom(Document document) {
        return boolForXPath(document, "string(/dbgp:response/@command)='eval'");
    }

    public EvalResponse(Document message) {
        super(message);
    }

    public boolean isSuccessful() {
        return "1".equals(xpath("/dbgp:response/@success"));
    }

    @Override
    public String getHandlerKey() {
        return getName() + ":" + getTransactionId();
    }

    public Optional<PropertyValue> getPropertyValue() {
        NodeList nodeList = nodeXpath("/dbgp:response/dbgp:property");
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            String name = XmlUtil.stringForXPath(node, "@name");
            String type = XmlUtil.stringForXPath(node, "@type");
            String value = XmlUtil.stringForXPath(node, "text()");
            PropertyValue propertyValue = new PropertyValue(name, type, value);
            return Optional.of(propertyValue);
        }
        return Optional.empty();
    }
}
