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

import com.codnos.dbgp.api.PropertyValue
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration.Builder._
import com.codnos.dbgp.internal.arguments.ArgumentFormat._
import com.codnos.dbgp.internal.commands.eval.{EvalCommand, EvalCommandHandler, EvalResponse}
import com.codnos.dbgp.internal.commands.run._
import com.codnos.dbgp.internal.xml.XmlUtil.parseMessage
import org.mockito.BDDMockito.given

class EvalSpec extends CommandSpec {


  val ValidResponse = <response xmlns="urn:debugger_protocol_v1"
                                xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
                                command="eval"
                                success="1"
                                transaction_id="TRANSACTION_ID">
    <property name="name" fullname="name" type="xs:string" encoding="none">abc</property>
  </response>
  val argumentConfiguration = configuration.withCommand("eval", numeric("i"), numeric("d"), string("-")).build

  "Command" should "have message constructed from the parameters" in {
    val command = new EvalCommand("456", 1, "$var/@attribute")

    command should have(
      'name ("eval"),
      'message ("eval -i 456 -d 1 -- JHZhci9AYXR0cmlidXRl"),
      'handlerKey ("eval:456")
    )
  }

  "Response" should "correctly expose all important attributes given xml" in {
    val response = new EvalResponse(parseMessage(ValidResponse.toString))

    response should have(
      'transactionId ("TRANSACTION_ID"),
      'handlerKey ("eval:TRANSACTION_ID")
    )
    response.isSuccessful shouldBe true
    response.getPropertyValue.isPresent shouldBe true
    val propertyValue = response.getPropertyValue.get()
    propertyValue.getName shouldBe "name"
    propertyValue.getType shouldBe "xs:string"
    propertyValue.getValue shouldBe "abc"
  }

  "CommandHandler" should "respond with either success or failure" in {
    val handler = new EvalCommandHandler(engine, argumentConfiguration)
    given(engine.eval(1, "$var/@attribute")).willReturn(Optional.of(new PropertyValue("name", "xs:string", "abc")))

    handler.channelRead(ctx, "eval -i 123 -d 1 -- JHZhci9AYXR0cmlidXRl")

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" command="eval"
                                     success="1"
                                     transaction_id="123">
      <property name="name" fullname="name" type="xs:string" encoding="none">abc</property>
    </response>
    assertReceived(expectedResponse)
  }
}
