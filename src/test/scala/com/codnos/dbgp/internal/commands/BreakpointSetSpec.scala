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

import com.codnos.dbgp.api.{Breakpoint, BreakpointType}
import com.codnos.dbgp.api.Breakpoint.{aConditionalBreakpoint, aCopyOf, aLineBreakpoint}
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration.Builder._
import com.codnos.dbgp.internal.arguments.ArgumentFormat._
import com.codnos.dbgp.internal.commands.breakpoint.{BreakpointSetCommand, BreakpointSetCommandHandler, BreakpointSetResponse}
import com.codnos.dbgp.internal.xml.XmlUtil._
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito._
import org.mockito.Matchers.any
import org.mockito.Mockito.verify

class BreakpointSetSpec extends CommandSpec {

  var ValidResponse = <response xmlns="urn:debugger_protocol_v1"
                                xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
                                command="breakpoint_set"
                                transaction_id="TRANSACTION_ID"
                                state="STATE"
                                id="BREAKPOINT_ID"/>
  val lineNumber = 555
  val fileUri = "file:///home/user/file.xq"
  val breakpointId = "myId"
  val originalBreakpoint: Breakpoint = aLineBreakpoint(fileUri, lineNumber).build()
  val breakpointThatWasSet: Breakpoint = aCopyOf(originalBreakpoint).withBreakpointId(breakpointId).build()
  val argumentConfiguration = configuration.withCommand("breakpoint_set", numeric("i"), string("t"), string("f"), numeric("n"), bool("r"), string("-")).build

  "Command" should "have message constructed from the parameters for line breakpoint" in {
    val command = new BreakpointSetCommand("432", originalBreakpoint)

    command should have (
      'name ("breakpoint_set"),
      'message ("breakpoint_set -i 432 -t line -f file:///home/user/file.xq -n 555"),
      'handlerKey ("breakpoint_set:432")
    )
  }

  it should "have message constructed from the parameters for conditional breakpoint" in {
    val conditionalBreakpoint = aConditionalBreakpoint(fileUri, lineNumber, "expr").build()
    val command = new BreakpointSetCommand("432", conditionalBreakpoint)

    command should have (
      'name ("breakpoint_set"),
      'message ("breakpoint_set -i 432 -t conditional -f file:///home/user/file.xq -n 555 -- ZXhwcg=="),
      'handlerKey ("breakpoint_set:432")
    )
  }

  it should "have message message with temporary flag on" in {
    val temporaryBreakpoint = aLineBreakpoint(fileUri, lineNumber).withTemporary(true).build()
    val command = new BreakpointSetCommand("432", temporaryBreakpoint)

    command should have (
      'name ("breakpoint_set"),
      'message ("breakpoint_set -i 432 -t line -f file:///home/user/file.xq -n 555 -r 1"),
      'handlerKey ("breakpoint_set:432")
    )
  }

  "Response" should "correctly expose all important attributes given xml" in {
    val response = new BreakpointSetResponse(parseMessage(ValidResponse.toString))

    response should have(
      'transactionId ("TRANSACTION_ID"),
      'state ("STATE"),
      'breakpointId ("BREAKPOINT_ID"),
      'handlerKey ("breakpoint_set:TRANSACTION_ID")
    )
  }

  it should "allow building it from valid xml" in {
    assert(breakpoint.BreakpointSetResponse.canBuildFrom(parseMessage(ValidResponse.toString)))
  }

  it should "not allow building it from xml for different command" in {
    assert(!breakpoint.BreakpointSetResponse.canBuildFrom(parseMessage(MadeUpCommandResponse.toString)))
  }

  "CommandHandler" should "respond with details of the breakpoint" in {
    val handler = new BreakpointSetCommandHandler(engine, argumentConfiguration)
    given(engine.breakpointSet(any())).willReturn(breakpointThatWasSet)

    handler.channelRead(ctx, "breakpoint_set -i 123 -t line -f " + fileUri + " -n " + lineNumber)

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" command="breakpoint_set"
                                     transaction_id="123"
                      state="enabled"
                      id={s"$breakpointId"}/>
    assertReceived(expectedResponse)
  }

  it  should "send breakpoint with temporary flag" in {
    val handler = new BreakpointSetCommandHandler(engine, argumentConfiguration)
    given(engine.breakpointSet(any())).willReturn(breakpointThatWasSet)

    handler.channelRead(ctx, "breakpoint_set -i 123 -t line -f " + fileUri + " -n " + lineNumber + " -r 1")

    val captor = ArgumentCaptor.forClass(classOf[Breakpoint])
    verify(engine).breakpointSet(captor.capture())
    val sentBreakpoint = captor.getValue
    sentBreakpoint.isTemporary shouldBe true
  }

  it  should "send conditional breakpoint" in {
    val handler = new BreakpointSetCommandHandler(engine, argumentConfiguration)
    given(engine.breakpointSet(any())).willReturn(breakpointThatWasSet)

    handler.channelRead(ctx, "breakpoint_set -i 123 -t conditional -f " + fileUri + " -n " + lineNumber + " -- ZXhwcg==")

    val captor = ArgumentCaptor.forClass(classOf[Breakpoint])
    verify(engine).breakpointSet(captor.capture())
    val sentBreakpoint = captor.getValue
    sentBreakpoint.getType shouldBe BreakpointType.CONDITIONAL
    sentBreakpoint.getLineNumber shouldBe Optional.of(lineNumber)
    sentBreakpoint.getFileURL shouldBe Optional.of(fileUri)
    sentBreakpoint.getExpression shouldBe Optional.of("expr")
  }
}
