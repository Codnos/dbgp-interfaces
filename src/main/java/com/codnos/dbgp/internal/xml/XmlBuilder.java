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

package com.codnos.dbgp.internal.xml;

import java.util.ArrayList;
import java.util.List;

public class XmlBuilder {
    private static final String DEFAULT_NAMESPACE = "";
    private final String elementName;
    private final String defaultElementNamespace;
    private final List<Attribute> attributes = new ArrayList<>();
    private final List<String> elements = new ArrayList<>();

    public static XmlBuilder e(String elementName) {
        return new XmlBuilder(elementName);
    }

    public static XmlBuilder e(String elementName, String defaultElementNamespace) {
        return new XmlBuilder(elementName, defaultElementNamespace);
    }

    public XmlBuilder a(String attributeName, String value) {
        this.attributes.add(new Attribute(attributeName, value));
        return this;
    }

    public XmlBuilder e(XmlBuilder elementBuilder) {
        this.elements.add(elementBuilder.asString());
        return this;
    }

    private XmlBuilder(String elementName) {
        this.elementName = elementName;
        this.defaultElementNamespace = DEFAULT_NAMESPACE;
    }

    private XmlBuilder(String elementName, String defaultElementNamespace) {
        this.elementName = elementName;
        this.defaultElementNamespace = defaultElementNamespace;
    }

    public String asString() {
        StringBuilder xml = new StringBuilder();
        String namespace = DEFAULT_NAMESPACE.equals(defaultElementNamespace) ? "" : " xmlns=\"" + defaultElementNamespace + "\"";
        openTag(xml);
        xml.append(elementName);
        xml.append(namespace);
        appendAttributes(xml);
        if (elements.size() > 0) {
            closeTagOpening(xml);
            appendElements(xml);
            closeNonEmptyTag(xml);
        } else {
            closeEmptyTag(xml);
        }
        return xml.toString();
    }

    private void closeNonEmptyTag(StringBuilder xml) {
        xml.append("</");
        xml.append(elementName);
        closeTagClosing(xml);
    }

    private void closeTagClosing(StringBuilder xml) {
        xml.append(">");
    }

    private void closeTagOpening(StringBuilder xml) {
        xml.append(">");
    }

    private void openTag(StringBuilder xml) {
        xml.append("<");
    }

    private void closeEmptyTag(StringBuilder xml) {
        xml.append("/>");
    }

    private void appendElements(StringBuilder xml) {
        for (String element : elements) {
            xml.append(element);
        }
    }

    private void appendAttributes(StringBuilder xml) {
        for (Attribute attribute : attributes) {
            xml.append(" ");
            xml.append(attribute.asString());
        }
    }

    private class Attribute {
        private final String attributeName;
        private final String value;

        Attribute(String attributeName, String value) {
            this.attributeName = attributeName;
            this.value = value;
        }

        String asString() {
            return attributeName + "=\"" + value + "\"";
        }
    }
}
