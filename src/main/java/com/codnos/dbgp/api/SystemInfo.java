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

package com.codnos.dbgp.api;

public class SystemInfo {
    private final String appId;
    private final String ideKey;
    private final String session;
    private final String language;
    private final String protocolVersion;
    private final String fileUri;

    public SystemInfo(String appId, String ideKey, String session, String language, String protocolVersion, String fileUri) {
        this.appId = appId;
        this.ideKey = ideKey;
        this.session = session;
        this.language = language;
        this.protocolVersion = protocolVersion;
        this.fileUri = fileUri;
    }

    public String getAppId() {
        return this.appId;
    }

    public String getIdeKey() {
        return this.ideKey;
    }

    public String getSession() {
        return this.session;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getFileUri() {
        return this.fileUri;
    }
}
