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

import com.codnos.dbgp.api.BreakpointUpdateData
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration.Builder._
import com.codnos.dbgp.internal.arguments.ArgumentFormat._
import com.codnos.dbgp.internal.commands.breakpoint.{BreakpointUpdateCommand, BreakpointUpdateCommandHandler, BreakpointUpdateResponse}
import com.codnos.dbgp.internal.xml.XmlUtil._
import org.mockito.Mockito

class BreakpointUpdateSpec extends CommandSpec {

  val ValidResponse = <response xmlns="urn:debugger_protocol_v1"
                                xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
                                command="breakpoint_update"
                                transaction_id="TRANSACTION_ID">
  </response>

  val argumentConfiguration = configuration.withCommand("breakpoint_update", numeric("i"), string("d"), string("s")).build

  "Command" should "have message constructed from the parameters" in {
    val command = new BreakpointUpdateCommand("432", "myId", new BreakpointUpdateData(false))

    command should have(
      'name ("breakpoint_update"),
      'message ("breakpoint_update -i 432 -d myId -s disabled"),
      'handlerKey ("breakpoint_update:432")
    )
  }

  "Response" should "correctly expose all important attributes given xml" in {
    val response = new BreakpointUpdateResponse(parseMessage(ValidResponse.toString))

    response should have(
      'transactionId ("TRANSACTION_ID"),
      'handlerKey ("breakpoint_update:TRANSACTION_ID")
    )
  }

  it should "allow building it from valid xml" in {
    assert(breakpoint.BreakpointUpdateResponse.canBuildFrom(parseMessage(ValidResponse.toString)))
  }

  it should "not allow building it from xml for different command" in {
    assert(!breakpoint.BreakpointUpdateResponse.canBuildFrom(parseMessage(MadeUpCommandResponse.toString)))
  }

  "CommandHandler" should "pass update details to the engine" in {
    val breakpointId = "myId"
    val handler = new BreakpointUpdateCommandHandler(engine, argumentConfiguration)

    handler.channelRead(ctx, "breakpoint_update -i 123 -d " + breakpointId + " -s disabled")

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" command="breakpoint_update"
                                     transaction_id="123">
    </response>
    assertReceived(expectedResponse)
    Mockito.verify(engine).breakpointUpdate(breakpointId, new BreakpointUpdateData(false))
  }
}
