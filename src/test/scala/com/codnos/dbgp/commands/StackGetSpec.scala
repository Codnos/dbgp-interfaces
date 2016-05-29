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

package com.codnos.dbgp.commands

import com.codnos.dbgp.api.StackFrame
import com.codnos.dbgp.commands.stack.StackGetCommand.{StackGetCommandHandler, StackGetResponse}
import com.codnos.dbgp.commands.stack.{StackDepthCommand, StackGetCommand}
import com.codnos.dbgp.xml.XmlUtil._
import org.mockito.BDDMockito._

class StackGetSpec extends CommandSpec {

  var ValidResponse = <response xmlns="urn:debugger_protocol_v1"
                                xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
                                command="stack_get"
                                transaction_id="transaction_id">
    <stack level="345"
           type="file|eval|?"
           filename="file:///home/user/file.xq"
           lineno="666"
           where="local:functionName()"
           cmdbegin="line_number:offset"
           cmdend="line_number:offset"/>
  </response>

  "Command" should "have message constructed from the parameters" in {
    val command = new StackGetCommand("456", 345)

    command should have(
      'name ("stack_get"),
      'message ("stack_get -i 456 -d 345"),
      'handlerKey ("stack_get:456")
    )
  }

  "Response" should "correctly expose all important attributes given xml" in {
    val response = new StackGetResponse(parseMessage(ValidResponse.toString))

    response should have(
      'transactionId ("transaction_id"),
      'fileUrl ("file:///home/user/file.xq"),
      'lineNumber (666),
      'where ("local:functionName()"),
      'handlerKey ("stack_get:transaction_id")
    )
  }

  it should "allow building it from valid xml" in {
    assert(StackGetCommand.StackGetResponse.canBuildFrom(parseMessage(ValidResponse.toString)))
  }

  it should "not allow building it from xml for different command" in {
    assert(!StackGetCommand.StackGetResponse.canBuildFrom(parseMessage(MadeUpCommandResponse.toString)))
  }

  "CommandHandler" should "respond with variables from given stack depth" in {
    val handler = new StackGetCommandHandler(engine)
    val frame = new StackFrame("file:///home/user/file.xq", 45, "local:functionName()")
    given(engine.getFrame(555)).willReturn(frame)

    handler.channelRead(ctx, "stack_get -i 456 -d 555")

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" command="stack_get"
                                     transaction_id="456">
      <stack level="555" type="file" filename="file:///home/user/file.xq" lineno="45" where="local:functionName()"/>
      </response>
    assertReceived(expectedResponse)
  }
}
