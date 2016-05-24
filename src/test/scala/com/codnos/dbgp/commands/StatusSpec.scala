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

import com.codnos.dbgp.commands.status.{State, Status}
import com.codnos.dbgp.xml.XmlUtil._
import org.mockito.BDDMockito.given

class StatusSpec extends CommandSpec {

  val ValidResponse =
    <response xmlns="urn:debugger_protocol_v1"
              xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
              command="status"
              status="starting"
              reason="ok"
              transaction_id="transaction_id">
      message data
    </response>

  "Command" should "have message constructed from the parameters" in {
    val command = new Status("432")

    command should have (
      'name ("status"),
      'message ("status -i 432"),
      'handlerKey ("status:432")
    )
  }

  "Response" should "correctly expose all important attributes given xml" in {
    val response = new Status.Response(parseMessage(ValidResponse.toString))

    response should have(
      'name ("status"),
      'transactionId ("transaction_id"),
      'handlerKey ("status:transaction_id")
    )
    response.getStatus.getState shouldBe "starting"
  }

  it should "allow building it from valid xml" in {
    assert(Status.Response.canBuildFrom(parseMessage(ValidResponse.toString)))
  }

  it should "not allow building it from xml for different command" in {
    assert(!Status.Response.canBuildFrom(parseMessage(MadeUpCommandResponse.toString)))
  }

  "CommandHandler" should "respond with variables from given stack depth" in {
    val handler = new Status.CommandHandler(engine)
    given(engine.getState).willReturn(State.RUNNING)

    handler.channelRead(ctx, "status -i 123")

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" transaction_id="123" command="status"
                                     status="running"
                                     reason="ok"/>
    assertReceived(expectedResponse)
  }
}
