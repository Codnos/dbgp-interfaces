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

package com.codnos.dbgp.internal.commands

import com.codnos.dbgp.api.DebuggerEngine
import org.mockito.BDDMockito._

import scala.xml.XML

class InitSpec extends CommandSpec {
  val ValidMessage = <init xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" appid="app-id-value" idekey="ide-key-value" session="session-id-value" language="language-value" protocol_version="1.0" fileuri="file:/home/user/module.xq"/>

  "Init" should "get all of the attributes from debugger engine and expose them" in {
    val debuggerEngine = mock[DebuggerEngine]
    given(debuggerEngine.getAppId).willReturn("app-id-value")
    given(debuggerEngine.getIdeKey).willReturn("ide-key-value")
    given(debuggerEngine.getSession).willReturn("session-id-value")
    given(debuggerEngine.getLanguage).willReturn("language-value")
    given(debuggerEngine.getProtocolVersion).willReturn("1.0")
    given(debuggerEngine.getInitialFileUri).willReturn("file:/home/user/module.xq")

    val init = new Init(debuggerEngine)

    assertXmlElementsAreEqual(ValidMessage, XML.loadString(init.asString()))
  }
}
