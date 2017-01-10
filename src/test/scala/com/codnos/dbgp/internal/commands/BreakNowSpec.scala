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

import com.codnos.dbgp.api.Breakpoint.aCopyOf
import com.codnos.dbgp.api.{BreakpointType, StatusChangeHandler}
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration.Builder._
import com.codnos.dbgp.internal.arguments.ArgumentFormat._
import com.codnos.dbgp.internal.commands.breakpoint.{BreakpointGetCommand, BreakpointGetCommandHandler, BreakpointGetResponse}
import com.codnos.dbgp.internal.commands.run._
import com.codnos.dbgp.internal.impl.StatusChangeHandlerFactory
import com.codnos.dbgp.internal.xml.XmlUtil.parseMessage
import org.mockito.BDDMockito.given
import org.mockito.Matchers._
import org.mockito.Mockito._

class BreakNowSpec extends CommandSpec {


  val ValidResponse = <response xmlns="urn:debugger_protocol_v1"
                                xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
                                command="break"
                                success="1"
                                transaction_id="TRANSACTION_ID">
  </response>
  val argumentConfiguration = configuration.withCommand("break", numeric("i")).build

  "Command" should "have message constructed from the parameters" in {
    val command = new BreakNowCommand("456")

    command should have(
      'name ("break"),
      'message ("break -i 456"),
      'handlerKey ("break:456")
    )
  }

  "Response" should "correctly expose all important attributes given xml" in {
    val response = new BreakNowResponse(parseMessage(ValidResponse.toString))

    response should have(
      'transactionId ("TRANSACTION_ID"),
      'handlerKey ("break:TRANSACTION_ID")
    )
    response.isSuccessful shouldBe true
  }

  "CommandHandler" should "respond with either success or failure" in {
    val handler = new BreakNowCommandHandler(engine, argumentConfiguration)
    given(engine.breakNow()).willReturn(true)

    handler.channelRead(ctx, "break -i 123")

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" command="break"
                                     success="1"
                                     transaction_id="123">
    </response>
    assertReceived(expectedResponse)
  }
}
