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

import com.codnos.dbgp.commands.breakpoint.{Breakpoint, BreakpointSet}
import com.codnos.dbgp.xml.XmlUtil._
import org.mockito.BDDMockito._
import org.mockito.Matchers.any

class BreakpointSetSpec extends CommandSpec {

  var ValidResponse = <response xmlns="urn:debugger_protocol_v1"
                                xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
                                command="breakpoint_set"
                                transaction_id="TRANSACTION_ID"
                                state="STATE"
                                id="BREAKPOINT_ID"/>
  val lineNumber = 555
  val fileUri = "file:///home/user/file.xq"
  val originalBreakpoint: Breakpoint = new Breakpoint(fileUri, lineNumber)

  "Command" should "have message constructed from the parameters" in {
    val command = new BreakpointSet("432", originalBreakpoint)

    command should have (
      'name ("breakpoint_set"),
      'message ("breakpoint_set -i 432 -t line -f file:///home/user/file.xq -n 555"),
      'handlerKey ("breakpoint_set:432")
    )
  }

  "Response" should "correctly expose all important attributes given xml" in {
    val response = new BreakpointSet.Response(parseMessage(ValidResponse.toString))

    response should have(
      'transactionId ("TRANSACTION_ID"),
      'state ("STATE"),
      'breakpointId ("BREAKPOINT_ID"),
      'handlerKey ("breakpoint_set:TRANSACTION_ID")
    )
  }

  it should "allow building it from valid xml" in {
    assert(BreakpointSet.Response.canBuildFrom(parseMessage(ValidResponse.toString)))
  }

  it should "not allow building it from xml for different command" in {
    assert(!BreakpointSet.Response.canBuildFrom(parseMessage(MadeUpCommandResponse.toString)))
  }

  "CommandHandler" should "respond with variables from given stack depth" in {
    val handler = new BreakpointSet.CommandHandler(engine)
    val breakpointId = s"${fileUri}@${lineNumber}"
    given(engine.breakpointSet(any())).willReturn(new Breakpoint(originalBreakpoint, breakpointId, "enabled"))

    handler.channelRead(ctx, "breakpoint_set -i 123 -t line -f " + fileUri + " -n " + lineNumber)

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" command="breakpoint_set"
                                     transaction_id="123"
                      state="enabled"
                      id={s"${breakpointId}"}/>
    assertReceived(expectedResponse)
  }
}