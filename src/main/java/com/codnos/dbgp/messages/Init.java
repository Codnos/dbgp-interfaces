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

package com.codnos.dbgp.messages;

import com.codnos.dbgp.xml.XmlUtil;
import org.w3c.dom.Document;

public class Init extends XmlMessage {
    public static boolean canBuildFrom(Document document) {
        return XmlUtil.boolForXPath(document, "boolean(/dbgp:init)");
    }

    public Init(Document message) {
        super(message);
    }

    public String getAppId() {
        return xpath("/dbgp:init/@appid");
    }

    public String getIdeKey() {
        return xpath("/dbgp:init/@idekey");
    }

    public String getSession() {
        return xpath("/dbgp:init/@session");
    }

    public String getLanguage() {
        return xpath("/dbgp:init/@language");
    }

    public String getProtocolVersion() {
        return xpath("/dbgp:init/@protocol_version");
    }

    public String getFileUri() {
        return xpath("/dbgp:init/@fileuri");
    }

    @Override
    public String getHandlerKey() {
        return "init:init";
    }
}
