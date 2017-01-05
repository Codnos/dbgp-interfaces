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

import com.codnos.dbgp.api.UnableToParseResponseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.Optional;

public class XmlUtil {
    public static Document parseMessage(String message) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return documentBuilder.parse(new ByteArrayInputStream(message.getBytes()));
        } catch (Exception e) {
            throw new UnableToParseResponseException(e);
        }
    }

    public static String stringForXPath(Node parsedMessage, String expression) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NamespaceContext namespaceContext = new DBGpNamespaceContext();
        xPath.setNamespaceContext(namespaceContext);
        try {
            XPathExpression xPathExpression = xPath.compile(expression);
            return xPathExpression.evaluate(parsedMessage);
        } catch (XPathExpressionException e) {
            throw new UnableToParseResponseException(e);
        }
    }

    public static Optional<String> optionalStringForXPath(Node parsedMessage, String xpath) {
        try {
            String value = stringForXPath(parsedMessage, xpath);
            if (value == null || value.length() == 0) {
                return Optional.empty();
            }
            return Optional.of(value);
        } catch (UnableToParseResponseException e) {
            return Optional.empty();
        }
    }


    public static Optional<Integer> optionalIntForXPath(Node parsedMessage, String xpath) {
        try {
            return Optional.ofNullable(intForXPath(parsedMessage, xpath));
        } catch (UnableToParseResponseException | NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static NodeList nodeForXPath(Document parsedMessage, String expression) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NamespaceContext namespaceContext = new DBGpNamespaceContext();
        xPath.setNamespaceContext(namespaceContext);
        try {
            XPathExpression xPathExpression = xPath.compile(expression);
            return (NodeList) xPathExpression.evaluate(parsedMessage, XPathConstants.NODESET);

        } catch (XPathExpressionException e) {
            throw new UnableToParseResponseException(e);
        }
    }

    public static Integer intForXPath(Node message, String expression) {
        return Integer.valueOf(stringForXPath(message, expression));
    }

    public static Boolean boolForXPath(Document message, String expression) {
        return Boolean.valueOf(stringForXPath(message, expression));
    }
}
