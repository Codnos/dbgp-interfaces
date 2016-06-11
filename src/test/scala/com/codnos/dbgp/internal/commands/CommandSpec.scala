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

import com.codnos.dbgp.UnitSpec
import com.codnos.dbgp.api.DebuggerEngine
import io.netty.channel.{ChannelFuture, ChannelHandlerContext}
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito._
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.xmlunit.builder.{DiffBuilder, Input}

import scala.xml.{Elem, XML}

abstract class CommandSpec extends UnitSpec {
  val MadeUpCommandResponse =
      <response xmlns="urn:debugger_protocol_v1"
                xmlns:xdebug="http://xdebug.org/dbgp/xdebug"
                command="random_command"
                param_id="param_id"
                transaction_id="transaction_id"/>

  val ctx = mock[ChannelHandlerContext]
  val channelFuture = mock[ChannelFuture]
  val engine = mock[DebuggerEngine]

  given(channelFuture.sync()).willReturn(channelFuture)
  given(ctx.writeAndFlush(any())).willReturn(channelFuture)

  def assertReceived(expectedResponse: Elem): Unit = {
    val captor = ArgumentCaptor.forClass(classOf[String])
    verify(ctx).writeAndFlush(captor.capture())
    val response = XML.loadString(captor.getValue)
    assertXmlElementsAreEqual(expectedResponse, response)
  }

  def assertXmlElementsAreEqual(expectedXml: Elem, actualXml: Elem): Unit = {
    val comparison = DiffBuilder.compare(Input.from(expectedXml.toString()))
      .withTest(Input.fromString(actualXml.toString()))
      .normalizeWhitespace()
      .build()
    assert(!comparison.hasDifferences, comparison.toString)
  }
}