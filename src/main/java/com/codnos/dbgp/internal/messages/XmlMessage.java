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

package com.codnos.dbgp.internal.messages;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import static com.codnos.dbgp.internal.xml.XmlUtil.intForXPath;
import static com.codnos.dbgp.internal.xml.XmlUtil.nodeForXPath;
import static com.codnos.dbgp.internal.xml.XmlUtil.stringForXPath;

public abstract class XmlMessage implements Message {
    private Document parsedMessage;

    public XmlMessage(Document message) {
        this.parsedMessage = message;
    }

    protected String xpath(String xpath) {
        return stringForXPath(parsedMessage, xpath);
    }

    protected Integer intXpath(String xpath) {
        return intForXPath(parsedMessage, xpath);
    }

    protected NodeList nodeXpath(String xpath) {
        return nodeForXPath(parsedMessage, xpath);
    }
}
