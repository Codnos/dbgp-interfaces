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

import java.util.Arrays.asList

import com.codnos.dbgp.api.PropertyValue
import com.codnos.dbgp.commands.context.ContextGetCommand
import com.codnos.dbgp.commands.context.ContextGetCommand.{ContextGetCommandHandler, ContextGetResponse}
import com.codnos.dbgp.xml.XmlUtil._
import org.mockito.BDDMockito.given

import scala.collection.JavaConversions._

class ContextGetSpec extends CommandSpec {

  val ValidResponse =
    <response xmlns="urn:debugger_protocol_v1"
              xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
              command="context_get"
              context="context_id"
              transaction_id="transaction_id">
      <property
      name="short_name"
      fullname="long_name"
      type="data_type"
      classname="name_of_object_class"
      constant="0"
      children="0"
      encoding="none">...encoded Value Data...</property>
    </response>

  "Command" should "have message constructed from the parameters" in {
    val command = new ContextGetCommand("432", 6)

    command should have (
      'name ("context_get"),
      'message ("context_get -i 432 -d 6"),
      'handlerKey ("context_get:432")
    )
  }

  "Response" should "correctly expose all important attributes given xml" in {
    val response = new ContextGetResponse(parseMessage(ValidResponse.toString))

    response should have(
      'name ("context_get"),
      'transactionId ("transaction_id"),
      'handlerKey ("context_get:transaction_id")
    )
    response.getVariables.head should have(
      'name ("short_name"),
      'type ("data_type"),
      'value ("...encoded Value Data...")
    )
  }

  it should "allow building it from valid xml" in {
    assert(ContextGetCommand.ContextGetResponse.canBuildFrom(parseMessage(ValidResponse.toString)))
  }

  it should "not allow building it from xml for different command" in {
    assert(!ContextGetCommand.ContextGetResponse.canBuildFrom(parseMessage(MadeUpCommandResponse.toString)))
  }

  "CommandHandler" should "respond with variables from given stack depth" in {
    val handler = new ContextGetCommandHandler(engine)
    val variables = asList(new PropertyValue("name", "xs:double", "1.0"))
    given(engine.getVariables(2)).willReturn(variables)

    handler.channelRead(ctx, "context_get -i 123 -d 2")

    val expectedResponse = <response xmlns="urn:debugger_protocol_v1" xmlns:xdebug="http://xdebug.org/dbgp/xdebug" transaction_id="123" command="context_get">
      <property name="name" fullname="name" type="xs:double" encoding="none">1.0</property>
    </response>
    assertReceived(expectedResponse)
  }
}
