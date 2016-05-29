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

import com.codnos.dbgp.api.State
import com.codnos.dbgp.commands.status.BreakingOrStoppingStateHandler

class BreakingOrStoppingStateHandlerSpec extends CommandSpec {

  "handler" should "not be applicable for not monitored state changes" in {
    val handler = new BreakingOrStoppingStateHandler("123", ctx)

    handler.applicableFor(State.STARTING, State.RUNNING) shouldBe false
    handler.applicableFor(State.BREAK, State.RUNNING) shouldBe false
    handler.applicableFor(State.BREAK, State.STOPPING) shouldBe false
  }

  it should "be applicable for monitored state changes" in {
    val handler = new BreakingOrStoppingStateHandler("123", ctx)

    handler.applicableFor(State.RUNNING, State.BREAK) shouldBe true
    handler.applicableFor(State.STOPPING, State.STOPPED) shouldBe true
  }

  it should "send the new state out" in {
    val handler = new BreakingOrStoppingStateHandler("123", ctx)

    handler.stateChanged(State.RUNNING, State.BREAK)


    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" transaction_id="123" command="status"
                                     status="break"
                                     reason="ok"/>
    assertReceived(expectedResponse)
  }
}
