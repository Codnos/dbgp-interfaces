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

package com.codnos.dbgp

import com.codnos.dbgp.commands.breakpoint.Breakpoint
import com.codnos.dbgp.commands.status.StatusValue
import com.codnos.dbgp.messages.InitMessage
import com.jayway.awaitility.Awaitility._
import org.hamcrest.Matchers._
import org.mockito.{BDDMockito, Matchers}
import org.mockito.Mockito._
import org.scalatest.{FeatureSpec, GivenWhenThen}

class DBGpFeatureSpec extends FeatureSpec with GivenWhenThen with AwaitilitySupport with org.scalatest.Matchers {
  private val Port: Int = 9000
  private val debuggerIde: FakeDebuggerIde = new FakeDebuggerIde
  private val debuggerEngine: DebuggerEngine = mock(classOf[DebuggerEngine])
  private val eventsHandler: DBGpEventsHandler = new DBGpEventsHandler

  feature("connection initiation") {
    scenario("<init/> message is sent as the first thing after engine connects to the IDE") {
      When("the engine and the IDE are connected")
      withinASession { _ =>

        Then("Init message is received")
        await until(() => debuggerIde.getInitMessage, is(notNullValue))
      }
    }
  }

  feature("setting breakpoints") {
    scenario("when breakpoint is set the debugger engine will receive the breakpoint") {
      Given("the engine and the IDE are connected")
      val breakpoint = new Breakpoint("file", 123)
      val breakpointAfterSetting = new Breakpoint(breakpoint, "id", "enabled")
      BDDMockito.given(debuggerEngine.breakpointSet(Matchers.any(classOf[Breakpoint]))).willReturn(breakpointAfterSetting)

      withinASession {
        ctx =>
          When("the breakpoint is set int the IDE")
          val result = ctx.ide.breakpointSet(breakpoint)

          Then("the engine receives the breakpoint")
          await until (() => verify(debuggerEngine).breakpointSet(Matchers.any(classOf[Breakpoint])))

          And("the breakpoint has correct attributes which engine has sent")
          result.getFileURL shouldBe breakpointAfterSetting.getFileURL
          result.getLineNumber shouldBe breakpointAfterSetting.getLineNumber
          result.getState shouldBe breakpointAfterSetting.getState
      }
    }
  }

  private class FakeDebuggerIde extends DebuggerIde {
    private var message: InitMessage = null

    def getInitMessage: InitMessage = {
      message
    }

    override def onConnected(message: InitMessage) {
      this.message = message
    }

    override def onStatus(status: StatusValue): Unit = ???
  }

  private def withinASession(f: DebuggingContext => Unit) {
    val ide = new DBGpIde(Port, eventsHandler)
    val engine = new DBGpEngine(Port, debuggerEngine)
    ide.registerIde(debuggerIde)
    ide.startListening()
    engine.connect()
    try {
      f(DebuggingContext(ide, engine))
    } finally {
      engine.disconnect()
      ide.stopListening()
    }
  }

  private case class DebuggingContext(ide: DBGpIde, engine: DBGpEngine)
}
