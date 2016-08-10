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

import com.codnos.dbgp.AwaitilitySupport
import com.codnos.dbgp.internal.handlers.ResponseSender
import com.codnos.dbgp.internal.impl.StatusChangeHandlerFactory
import com.jayway.awaitility.Awaitility._
import org.hamcrest.Matchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.{BDDMockito, Matchers, Mockito}
import org.scalatest.{BeforeAndAfter, FeatureSpec}

class DBGpFeatureSpec extends FeatureSpec with AwaitilitySupport with org.scalatest.Matchers with BeforeAndAfter {
  private val statusChangeHandlerFactory = new SpyingStatusChangeHandlerFactory()
  private val Port: Int = 9000
  private val debuggerIde: FakeDebuggerIde = new FakeDebuggerIde
  private val debuggerEngine: DebuggerEngine = mock(classOf[DebuggerEngine])

  after {
    statusChangeHandlerFactory.lastStatusChangeHandler = null
    debuggerIde.clear()
    Mockito.reset(debuggerEngine)
  }

  feature("connection initiation") {
    scenario("<init/> message is sent as the first thing after engine connects to the IDE") {
      withinASession { _ =>
        await until(() => debuggerIde.getInitMessage, is(notNullValue(classOf[SystemInfo])))
      }
    }
  }

  feature("setting breakpoints") {
    scenario("when breakpoint is set the debugger engine will receive the breakpoint and respond with details") {
      val breakpoint = new Breakpoint("file", 123)
      val breakpointAfterSetting = new Breakpoint(breakpoint, "id", "other")
      BDDMockito.given(debuggerEngine.breakpointSet(Matchers.any(classOf[Breakpoint]))).willReturn(breakpointAfterSetting)

      withinASession {
        ctx =>
          val result = ctx.ide.breakpointSet(breakpoint)

          await until { verify(debuggerEngine).breakpointSet(Matchers.any(classOf[Breakpoint])) }

          result.getFileURL shouldBe breakpointAfterSetting.getFileURL
          result.getLineNumber shouldBe breakpointAfterSetting.getLineNumber
          result.getState shouldBe breakpointAfterSetting.getState
          result.getBreakpointId shouldBe breakpointAfterSetting.getBreakpointId
      }
    }
  }

  feature("running the code") {
    scenario("after initiating the session the code can be run") {
      withinASession {
        ctx =>
          ctx.ide.run()
          await until { verify(debuggerEngine).registerStatusChangeHandler(Matchers.any(classOf[StatusChangeHandler])) }
          await until { verify(debuggerEngine).run() }
      }
    }
    scenario("after the code was run when the breakpoint is hit we receive notification of status change") {
      BDDMockito.given(debuggerEngine.run()).will(new Answer[Unit] {
        override def answer(invocation: InvocationOnMock): Unit = {
          statusChangeHandlerFactory.lastStatusChangeHandler.statusChanged(Status.RUNNING, Status.BREAK)
        }
      })

      withinASession {
        ctx =>
          ctx.ide.run()
          await until { assert(debuggerIde.getStatus == Status.BREAK) }
      }
    }
  }

  feature("checking current status") {
    scenario("after initiating the session the status can be checked") {
      val expectedStatus = Status.RUNNING
      BDDMockito.given(debuggerEngine.getStatus).willReturn(expectedStatus)

      withinASession {
        ctx =>
          val status = ctx.ide.status()
          await until { verify(debuggerEngine).getStatus }
          status shouldBe expectedStatus
      }
    }
  }

  feature("checking stack depth") {
    scenario("after initiating the session the stack depth can be checked") {
      BDDMockito.given(debuggerEngine.getStackDepth).willReturn(7)

      withinASession {
        ctx =>
          val stackDepth = ctx.ide.stackDepth()
          await until { verify(debuggerEngine).getStackDepth }
          stackDepth shouldBe 7
      }
    }
  }

  feature("checking stack frame") {
    scenario("after initiating the session the stack frame can be checked") {
      val frame = new StackFrame("uri", 88, "method")
      BDDMockito.given(debuggerEngine.getFrame(7)).willReturn(frame)

      withinASession {
        ctx =>
          val result = ctx.ide.stackGet(7)
          await until { verify(debuggerEngine).getFrame(7) }
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

      withinASession {
        ctx =>
          val result = ctx.ide.contextGet(7)
          await until { verify(debuggerEngine).getVariables(7) }
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
      withinASession {
        ctx =>
          ctx.ide.stepOver()
          await until { verify(debuggerEngine).registerStatusChangeHandler(Matchers.any(classOf[StatusChangeHandler])) }
          await until { verify(debuggerEngine).stepOver()}
      }
    }
    scenario("after stepping over the code we get notified about any status changes") {
      BDDMockito.given(debuggerEngine.stepOver()).will(new Answer[Unit] {
        override def answer(invocation: InvocationOnMock): Unit = {
          statusChangeHandlerFactory.lastStatusChangeHandler.statusChanged(Status.RUNNING, Status.BREAK)
        }
      })

      withinASession {
        ctx =>
          ctx.ide.stepOver()
          await until { assert(debuggerIde.getStatus == Status.BREAK) }
      }
    }
  }

  feature("stepping into the code") {
    scenario("after initiating the session the code can be stepped into") {
      withinASession {
        ctx =>
          ctx.ide.stepInto()
          await until { verify(debuggerEngine).registerStatusChangeHandler(Matchers.any(classOf[StatusChangeHandler])) }
          await until { verify(debuggerEngine).stepInto() }
      }
    }
    scenario("after stepping into the code we get notified about any status changes") {
      BDDMockito.given(debuggerEngine.stepInto()).will(new Answer[Unit] {
        override def answer(invocation: InvocationOnMock): Unit = {
          statusChangeHandlerFactory.lastStatusChangeHandler.statusChanged(Status.RUNNING, Status.BREAK)
        }
      })

      withinASession {
        ctx =>
          ctx.ide.stepInto()
          await until { assert(debuggerIde.getStatus == Status.BREAK) }
      }
    }
  }

  feature("stepping out of the code") {
    scenario("after initiating the session the code can be stepped out of") {
      withinASession {
        ctx =>
          ctx.ide.stepOut()
          await until { verify(debuggerEngine).registerStatusChangeHandler(Matchers.any(classOf[StatusChangeHandler])) }
          await until { verify(debuggerEngine).stepOut() }
      }
    }
    scenario("after stepping out of the code we get notified about any status changes") {
      BDDMockito.given(debuggerEngine.stepOut()).will(new Answer[Unit] {
        override def answer(invocation: InvocationOnMock): Unit = {
          statusChangeHandlerFactory.lastStatusChangeHandler.statusChanged(Status.RUNNING, Status.BREAK)
        }
      })

      withinASession {
        ctx =>
          ctx.ide.stepOut()
          await until { assert(debuggerIde.getStatus == Status.BREAK) }
      }
    }
  }

  private class FakeDebuggerIde extends DebuggerIde {
    private var message: SystemInfo = _
    private var status: Status = _

    def getInitMessage: SystemInfo = message

    def getStatus: Status = status

    override def onConnected(message: SystemInfo) {
      this.message = message
    }

    override def onStatus(status: Status, dbgpIde: DBGpIde) {
      this.status = status
    }

    def clear(): Unit = {
      message = null
      status = null
    }
  }

  private def withinASession(f: DebuggingContext => Unit) {
    val ide: DBGpIde = DBGpFactory.ide().withPort(Port).withDebuggerIde(debuggerIde).build()
    val engine: DBGpEngine = DBGpFactory.engine.withPort(Port).withDebuggerEngine(debuggerEngine).withStatusChangeHandlerFactory(statusChangeHandlerFactory).build()
    ide.startListening()
    await until { ide.isConnected }
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
    var lastStatusChangeHandler: StatusChangeHandler = _

    override def getInstance(transactionId: Int, responseSender: ResponseSender): StatusChangeHandler = {
      lastStatusChangeHandler = super.getInstance(transactionId, responseSender)
      lastStatusChangeHandler
    }
  }

}
