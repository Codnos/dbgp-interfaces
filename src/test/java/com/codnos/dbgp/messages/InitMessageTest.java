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

import org.junit.Test;

import static com.codnos.dbgp.xml.XmlUtil.parseMessage;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class InitMessageTest {

    @Test
    public void shouldExtractAllTheDataFromTheMessage() {
        String message = "<init xmlns=\"urn:debugger_protocol_v1\" xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\" appid=\"app-id-value\" idekey=\"ide-key-value\" session=\"session-id-value\" language=\"language-value\" protocol_version=\"1.0\" fileuri=\"file:/home/user/module.xq\"/>";

        InitMessage initMessage = new InitMessage(parseMessage(message));

        assertThat(initMessage.getAppId(), equalTo("app-id-value"));
        assertThat(initMessage.getIdeKey(), equalTo("ide-key-value"));
        assertThat(initMessage.getSession(), equalTo("session-id-value"));
        assertThat(initMessage.getLanguage(), equalTo("language-value"));
        assertThat(initMessage.getProtocolVersion(), equalTo("1.0"));
        assertThat(initMessage.getFileUri(), equalTo("file:/home/user/module.xq"));
    }
}
