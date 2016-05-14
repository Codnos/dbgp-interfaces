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

package com.codnos.dbgp.messages

import com.codnos.dbgp.UnitSpec
import com.codnos.dbgp.xml.XmlUtil._

class InitMessageSpec extends UnitSpec {
  val ValidMessage = <init xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" appid="app-id-value" idekey="ide-key-value" session="session-id-value" language="language-value" protocol_version="1.0" fileuri="file:/home/user/module.xq"/>
  val OtherMessage = <message/>

  "InitMessage" should "expose all relevant attributes from xml" in {

    val initMessage = new InitMessage(parseMessage(ValidMessage.toString))

    initMessage should have (
      'appId ("app-id-value"),
      'ideKey ("ide-key-value"),
      'session ("session-id-value"),
      'language ("language-value"),
      'protocolVersion ("1.0"),
      'fileUri ("file:/home/user/module.xq"),
      'handlerKey ("init:init")
    )
  }

  it should "allow building it from valid xml" in {
    assert(InitMessage.canBuildFrom(parseMessage(ValidMessage.toString)))
  }

  it should "not allow building it from xml for different command" in {
    assert(!InitMessage.canBuildFrom(parseMessage(OtherMessage.toString)))
  }
}
