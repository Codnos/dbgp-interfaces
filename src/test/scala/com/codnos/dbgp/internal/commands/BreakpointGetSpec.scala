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

import com.codnos.dbgp.api.Breakpoint.{aCopyOf, aLineBreakpoint}
import com.codnos.dbgp.api.{Breakpoint, BreakpointType}
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration.Builder._
import com.codnos.dbgp.internal.arguments.ArgumentFormat._
import com.codnos.dbgp.internal.commands.breakpoint._
import com.codnos.dbgp.internal.xml.XmlUtil._
import org.mockito.BDDMockito._

class BreakpointGetSpec extends CommandSpec {

  val ValidResponse = <response xmlns="urn:debugger_protocol_v1"
                                xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
                                command="breakpoint_get"
                                transaction_id="TRANSACTION_ID">
    <breakpoint id="BREAKPOINT_ID"
                type="line"
                state="enabled"
                filename="FILENAME"
                lineno="555">
    </breakpoint>
  </response>
  val ValidResponseForTemporary = <response xmlns="urn:debugger_protocol_v1"
                                            xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
                                            command="breakpoint_get"
                                            transaction_id="TRANSACTION_ID">
    <breakpoint id="BREAKPOINT_ID"
                type="line"
                state="enabled"
                temporary="1"
                filename="FILENAME"
                lineno="555">
    </breakpoint>
  </response>
  val lineNumber = 555
  val fileUri = "file:///home/user/file.xq"
  val originalBreakpoint: Breakpoint = aLineBreakpoint(fileUri, lineNumber).build()
  val breakpointAfterSetting: Breakpoint = aCopyOf(originalBreakpoint).withBreakpointId("myId").build()
  val argumentConfiguration = configuration.withCommand("breakpoint_get", numeric("i"), string("d")).build

  "Command" should "have message constructed from the parameters" in {
    val command = new BreakpointGetCommand("432", "myId")

    command should have(
      'name ("breakpoint_get"),
      'message ("breakpoint_get -i 432 -d myId"),
      'handlerKey ("breakpoint_get:432")
    )
  }

  "Response" should "correctly expose all important attributes given xml" in {
    val response = new BreakpointGetResponse(parseMessage(ValidResponse.toString))

    response should have(
      'transactionId ("TRANSACTION_ID"),
      'handlerKey ("breakpoint_get:TRANSACTION_ID")
    )
    val breakpoint = response.getBreakpoint
    breakpoint.isEnabled shouldBe true
    breakpoint.isTemporary shouldBe false
    breakpoint should have(
      'breakpointId ("BREAKPOINT_ID"),
      'type (BreakpointType.LINE),
      'fileURL (Optional.of("FILENAME")),
      'lineNumber (Optional.of(555)),
      'function (Optional.empty()),
      'exception (Optional.empty()),
      'expression (Optional.empty()),
      'hitValue (Optional.empty()),
      'hitCondition (Optional.empty()),
      'hitCount (Optional.empty())
    )
  }

  it should "correctly mark that breakpoint is temporary" in {
    val response = new BreakpointGetResponse(parseMessage(ValidResponseForTemporary.toString))

    response should have(
      'transactionId ("TRANSACTION_ID"),
      'handlerKey ("breakpoint_get:TRANSACTION_ID")
    )
    val breakpoint = response.getBreakpoint
    breakpoint.isEnabled shouldBe true
    breakpoint.isTemporary shouldBe true
  }

  it should "allow building it from valid xml" in {
    assert(breakpoint.BreakpointGetResponse.canBuildFrom(parseMessage(ValidResponse.toString)))
  }

  it should "not allow building it from xml for different command" in {
    assert(!breakpoint.BreakpointGetResponse.canBuildFrom(parseMessage(MadeUpCommandResponse.toString)))
  }

  "CommandHandler" should "respond with details of the breakpoint" in {
    val handler = new BreakpointGetCommandHandler(engine, argumentConfiguration)
    val breakpointId = s"${fileUri}@${lineNumber}"
    given(engine.breakpointGet(breakpointId)).willReturn(aCopyOf(originalBreakpoint).withBreakpointId(breakpointId).build())

    handler.channelRead(ctx, "breakpoint_get -i 123 -d " + breakpointId)

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" command="breakpoint_get"
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
}
