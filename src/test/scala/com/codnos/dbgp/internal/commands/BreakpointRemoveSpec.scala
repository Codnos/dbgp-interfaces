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

import java.util.Optional

import com.codnos.dbgp.api.Breakpoint
import com.codnos.dbgp.api.Breakpoint.{aCopyOf, aLineBreakpoint}
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration.Builder._
import com.codnos.dbgp.internal.arguments.ArgumentFormat._
import com.codnos.dbgp.internal.commands.breakpoint._
import com.codnos.dbgp.internal.xml.XmlUtil._
import org.mockito.BDDMockito._
import org.mockito.Matchers.any

class BreakpointRemoveSpec extends CommandSpec {

  var ValidResponse = <response xmlns="urn:debugger_protocol_v1"
                                xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
                                command="breakpoint_remove"
                                transaction_id="TRANSACTION_ID"/>
  val lineNumber = 555
  val fileUri = "file:///home/user/file.xq"
  val breakpointId = "myId"
  val originalBreakpoint: Breakpoint = aLineBreakpoint(fileUri, lineNumber).build()
  val breakpointThatWasSet: Breakpoint = aCopyOf(originalBreakpoint).withBreakpointId(breakpointId).build()
  val argumentConfiguration = configuration.withCommand("breakpoint_remove", numeric("i"), string("d")).build

  "Command" should "have message constructed from the parameters" in {
    val command = new BreakpointRemoveCommand("432", "myId")

    command should have (
      'name ("breakpoint_remove"),
      'message ("breakpoint_remove -i 432 -d myId"),
      'handlerKey ("breakpoint_remove:432")
    )
  }

  "Response" should "correctly expose all important attributes given xml" in {
    val response = new BreakpointRemoveResponse(parseMessage(ValidResponse.toString))

    response should have(
      'transactionId ("TRANSACTION_ID"),
      'handlerKey ("breakpoint_remove:TRANSACTION_ID")
    )
  }

  it should "allow building it from valid xml" in {
    assert(breakpoint.BreakpointRemoveResponse.canBuildFrom(parseMessage(ValidResponse.toString)))
  }

  it should "not allow building it from xml for different command" in {
    assert(!breakpoint.BreakpointRemoveResponse.canBuildFrom(parseMessage(MadeUpCommandResponse.toString)))
  }

  "CommandHandler" should "respond with breakpoint data when returned by engine" in {
    val handler = new BreakpointRemoveCommandHandler(engine, argumentConfiguration)
    given(engine.breakpointRemove(any())).willReturn(Optional.of(breakpointThatWasSet))

    handler.channelRead(ctx, "breakpoint_remove -i 123 -d " + breakpointId)

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" command="breakpoint_remove"
                                     transaction_id="123">
                            <breakpoint id={s"$breakpointId"}
                                        type="line"
                                        state="enabled"
                                        filename={s"$fileUri"}
                                        lineno={s"$lineNumber"}>
                              </breakpoint>
                           </response>
    assertReceived(expectedResponse)
  }

  it should "respond without breakpoint data when not returned by engine" in {
    val handler = new BreakpointRemoveCommandHandler(engine, argumentConfiguration)
    val breakpointId = s"${fileUri}@${lineNumber}"
    given(engine.breakpointRemove(any())).willReturn(Optional.empty[Breakpoint]())

    handler.channelRead(ctx, "breakpoint_remove -i 123 -d " + breakpointId)

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" command="breakpoint_remove"
                                     transaction_id="123"/>
    assertReceived(expectedResponse)
  }
}
