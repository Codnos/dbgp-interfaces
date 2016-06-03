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

import com.codnos.dbgp.internal.commands.stack.{StackDepthCommand, StackDepthCommandHandler, StackDepthResponse}
import com.codnos.dbgp.internal.xml.XmlUtil._
import org.mockito.BDDMockito._

class StackDepthSpec extends CommandSpec {

  var ValidResponse = <response xmlns="urn:debugger_protocol_v1"
                                xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
                                command="stack_depth"
                                depth="986"
                                transaction_id="transaction_id"/>

  "Command" should "have message constructed from the parameters" in {
    val command = new StackDepthCommand("456")

    command should have (
      'name ("stack_depth"),
      'message ("stack_depth -i 456"),
      'handlerKey ("stack_depth:456")
    )
  }

  "Response" should "correctly expose all important attributes given xml" in {
    val response = new StackDepthResponse(parseMessage(ValidResponse.toString))

    response should have(
      'transactionId ("transaction_id"),
      'depth (986),
      'handlerKey ("stack_depth:transaction_id")
    )
  }

  it should "allow building it from valid xml" in {
    assert(stack.StackDepthResponse.canBuildFrom(parseMessage(ValidResponse.toString)))
  }

  it should "not allow building it from xml for different command" in {
    assert(!stack.StackDepthResponse.canBuildFrom(parseMessage(MadeUpCommandResponse.toString)))
  }

  "CommandHandler" should "respond with variables from given stack depth" in {
    val handler = new StackDepthCommandHandler(engine)
    given(engine.getStackDepth()).willReturn(643)

    handler.channelRead(ctx, "stack_depth -i 456")

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" command="stack_depth"
                                     transaction_id="456"
                      depth="643"/>
    assertReceived(expectedResponse)
  }
}
