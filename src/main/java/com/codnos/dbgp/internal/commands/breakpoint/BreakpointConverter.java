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
import com.codnos.dbgp.internal.xml.XmlBuilder;
import org.w3c.dom.Node;

import static com.codnos.dbgp.api.Breakpoint.aBreakpoint;
import static com.codnos.dbgp.internal.xml.XmlBuilder.e;
import static com.codnos.dbgp.internal.xml.XmlUtil.optionalIntForXPath;
import static com.codnos.dbgp.internal.xml.XmlUtil.optionalStringForXPath;
import static com.codnos.dbgp.internal.xml.XmlUtil.stringForXPath;

public class BreakpointConverter {
    public static Breakpoint breakpointFromNode(Node breakpointNode) {
        return aBreakpoint()
                .withBreakpointId(stringForXPath(breakpointNode, "@id"))
                .withEnabled(!"disabled".equals(stringForXPath(breakpointNode, "@state")))
                .withTemporary("1".equals(stringForXPath(breakpointNode, "@temporary")))
                .withType(stringForXPath(breakpointNode, "@type"))
                .withFileUri(optionalStringForXPath(breakpointNode, "@filename"))
                .withLineNumber(optionalIntForXPath(breakpointNode, "@lineno"))
                .withFunction(optionalStringForXPath(breakpointNode, "@function"))
                .withException(optionalStringForXPath(breakpointNode, "@exception"))
                .withExpression(optionalStringForXPath(breakpointNode, "dbgp:expression/text()"))
                .withHitValue(optionalStringForXPath(breakpointNode, "@hit_value"))
                .withHitCondition(optionalStringForXPath(breakpointNode, "@hit_condition"))
                .withHitCount(optionalIntForXPath(breakpointNode, "@hit_count"))
                .build();
    }

    public static XmlBuilder breakpointToXml(Breakpoint breakpoint) {
        XmlBuilder breakpointXml = e("breakpoint")
                .a("id", breakpoint.getBreakpointId())
                .a("state", breakpointStateAsString(breakpoint))
                .a("type", breakpoint.getType().asString())
                ;
        switch (breakpoint.getType()) {
            case LINE:
                breakpointXml.a("filename", breakpoint.getFileURL().get()).a("lineno", breakpoint.getLineNumber().get());
                break;
        }
        return breakpointXml;
    }

    public static String breakpointStateAsString(Breakpoint breakpoint) {
        return breakpointStateAsString(breakpoint.isEnabled());
    }

    public static String breakpointStateAsString(boolean enabled) {
        return enabled ? "enabled" : "disabled";
    }

    public static boolean breakpointStateFromString(String state) {
        return "enabled".equals(state);
    }
}
