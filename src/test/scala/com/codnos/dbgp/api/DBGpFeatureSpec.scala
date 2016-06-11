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

package com.codnos.dbgp.api

import java.util

import com.codnos.dbgp.internal.impl.StatusChangeHandlerFactory
import com.codnos.dbgp.AwaitilitySupport
import com.jayway.awaitility.Awaitility._
import io.netty.channel.ChannelHandlerContext
import org.hamcrest.Matchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.{BDDMockito, Matchers}
import org.scalatest.{FeatureSpec, GivenWhenThen}

class DBGpFeatureSpec extends FeatureSpec with GivenWhenThen with AwaitilitySupport with org.scalatest.Matchers {
  private val statusChangeHandlerFactory = new SpyingStatusChangeHandlerFactory()
  private val Port: Int = 9000
  private val debuggerIde: FakeDebuggerIde = new FakeDebuggerIde
  private val debuggerEngine: DebuggerEngine = mock(classOf[DebuggerEngine])

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
    scenario("when breakpoint is set the debugger engine will receive the breakpoint and respond with details") {
      val breakpoint = new Breakpoint("file", 123)
      val breakpointAfterSetting = new Breakpoint(breakpoint, "id", "enabled")
      BDDMockito.given(debuggerEngine.breakpointSet(Matchers.any(classOf[Breakpoint]))).willReturn(breakpointAfterSetting)

      Given("the engine and the IDE are connected")
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

  feature("running the code") {
    scenario("after initiating the session the code can be run") {
      Given("the engine and the IDE are connected")
      withinASession {
        ctx =>
          When("the run command is sent")
          ctx.ide.run()
          Then("the debugger engine will get notified about any status changes")
          await until (() => verify(debuggerEngine).registerStatusChangeHandler(Matchers.any(classOf[StatusChangeHandler])))
          And("the debugger engine receives the run command")
          await until (() => verify(debuggerEngine).run())
      }
    }
    scenario("after the code was run when the breakpoint is hit we receive notification of status change") {
      BDDMockito.given(debuggerEngine.run()).will(new Answer[Unit] {
        override def answer(invocation: InvocationOnMock): Unit = {
          statusChangeHandlerFactory.lastStatusChangeHandler.statusChanged(Status.RUNNING, Status.BREAK)
        }
      })

      Given("the engine and the IDE are connected")
      withinASession {
        ctx =>
          When("the run command is sent")
          ctx.ide.run()
          Then("the IDE engine will get notified about any status changes")
          await until (() => assert(debuggerIde.getStatus == Status.BREAK))
      }
    }
  }

  feature("checking current status") {
    scenario("after initiating the session the status can be checked") {
      val expectedStatus = Status.RUNNING
      BDDMockito.given(debuggerEngine.getStatus).willReturn(expectedStatus)

      Given("the engine and the IDE are connected")
      withinASession {
        ctx =>
          When("the status command is sent")
          val status = ctx.ide.status()
          Then("the debugger engine receives the status command")
          await until (() => verify(debuggerEngine).getStatus)
          And("status is the same as returned by the engine")
          status shouldBe expectedStatus
      }
    }
  }

  feature("checking stack depth") {
    scenario("after initiating the session the stack depth can be checked") {
      BDDMockito.given(debuggerEngine.getStackDepth).willReturn(7)

      Given("the engine and the IDE are connected")
      withinASession {
        ctx =>
          When("the stack depth command is sent")
          val stackDepth = ctx.ide.stackDepth()
          Then("the debugger engine receives the stack depth command")
          await until (() => verify(debuggerEngine).getStackDepth)
          And("stack depth is the same as returned by the engine")
          stackDepth shouldBe 7
      }
    }
  }

  feature("checking stack frame") {
    scenario("after initiating the session the stack frame can be checked") {
      val frame = new StackFrame("uri", 88, "method")
      BDDMockito.given(debuggerEngine.getFrame(7)).willReturn(frame)

      Given("the engine and the IDE are connected")
      withinASession {
        ctx =>
          When("the stack get command is sent")
          val result = ctx.ide.stackGet(7)
          Then("the debugger engine receives the stack get command")
          await until (() => verify(debuggerEngine).getFrame(7))
          And("stack frame is the same as returned by the engine")
          result should have(
            'fileURL ("uri"),
            'lineNumber (88),
            'where ("method")
          )
      }
    }
  }

  feature("checking context") {
    scenario("after initiating the session the context can be checked") {
      val variables = util.Arrays.asList(new PropertyValue("abc", "int", "989"))
      BDDMockito.given(debuggerEngine.getVariables(7)).willReturn(variables)

      Given("the engine and the IDE are connected")
      withinASession {
        ctx =>
          When("the stackDepth command is sent")
          val result = ctx.ide.contextGet(7)
          Then("the debugger engine receives the context get command")
          await until (() => verify(debuggerEngine).getVariables(7))
          And("stack frame is the same as returned by the engine")
          val variablesFound = result.getVariables.toArray()
          variablesFound should have size 1
          variablesFound(0) should have(
            'name ("abc"),
            'type ("int"),
            'value ("989")
          )
      }
    }
  }

  feature("stepping over the code") {
    scenario("after initiating the session the code can be stepped over") {
      Given("the engine and the IDE are connected")
      withinASession {
        ctx =>
          When("the step over command is sent")
          ctx.ide.stepOver()
          Then("the debugger engine will get notified about any status changes")
          await until (() => verify(debuggerEngine).registerStatusChangeHandler(Matchers.any(classOf[StatusChangeHandler])))
          And("the debugger engine receives the step over command")
          await until (() => verify(debuggerEngine).stepOver())
      }
    }
    scenario("after stepping over the code we get notified about any status changes") {
      BDDMockito.given(debuggerEngine.stepOver()).will(new Answer[Unit] {
        override def answer(invocation: InvocationOnMock): Unit = {
          statusChangeHandlerFactory.lastStatusChangeHandler.statusChanged(Status.RUNNING, Status.BREAK)
        }
      })

      Given("the engine and the IDE are connected")
      withinASession {
        ctx =>
          When("the step over command is sent")
          ctx.ide.stepOver()
          Then("the IDE will get notified about any status changes")
          await until (() => assert(debuggerIde.getStatus == Status.BREAK))
      }
    }
  }

  private class FakeDebuggerIde extends DebuggerIde {
    private var message: SystemInfo = null
    private var status: Status = null

    def getInitMessage: SystemInfo = message

    def getStatus: Status = status

    override def onConnected(message: SystemInfo) {
      this.message = message
    }

    override def onStatus(status: Status, dbgpIde: DBGpIde) {
      this.status = status
    }
  }

  private def withinASession(f: DebuggingContext => Unit) {
    val ide: DBGpIde = DBGpFactory.ide().withPort(Port).withDebuggerIde(debuggerIde).build()
    val engine: DBGpEngine = DBGpFactory.engine.withPort(Port).withDebuggerEngine(debuggerEngine).withStatusChangeHandlerFactory(statusChangeHandlerFactory).build()
    ide.startListening()
    await until {
      ide.isConnected
    }
    engine.connect()
    try {
      f(DebuggingContext(ide, engine))
    } finally {
      engine.disconnect()
      ide.stopListening()
    }
  }

  private case class DebuggingContext(ide: DBGpIde, engine: DBGpEngine)

  class SpyingStatusChangeHandlerFactory() extends StatusChangeHandlerFactory {
    var lastStatusChangeHandler: StatusChangeHandler = null

    override def getInstance(transactionId: Int, ctx: ChannelHandlerContext): StatusChangeHandler = {
      lastStatusChangeHandler = super.getInstance(transactionId, ctx)
      lastStatusChangeHandler
    }
  }

}
