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

package com.codnos.dbgp.internal.xml

import com.codnos.dbgp.UnitSpec
import com.codnos.dbgp.internal.xml.XmlBuilder.e
import org.xmlunit.builder.{DiffBuilder, Input}

import scala.xml.Elem

class XmlBuilderSpec extends UnitSpec {

  "builder" should "construct xml document root" in {
    val expectedXml = <document_root/>

    val xml = e("document_root").asString()

    assertXmlElementsAreEqual(expectedXml, xml)
  }

  "it" should "construct root element with default namespace" in {
    val expectedXml = <document_root xmlns="urn:debugger_protocol_v1"/>

    val xml = e("document_root", "urn:debugger_protocol_v1").asString()

    assertXmlElementsAreEqual(expectedXml, xml)
  }

  "it" should "construct root element with attributes" in {
    val expectedXml = <document_root xmlns="urn:debugger_protocol_v1" abc="value123" def="10.1"/>

    val xml = e("document_root", "urn:debugger_protocol_v1")
      .a("abc", "value123")
      .a("def", "10.1")
      .asString()

    assertXmlElementsAreEqual(expectedXml, xml)
  }

  "it" should "construct root element with inner elements" in {
    val expectedXml = <document_root xmlns="urn:debugger_protocol_v1" abc="value123" def="10.1">
      <inner/>
      <second some="value"/>
    </document_root>

    val xml = e("document_root", "urn:debugger_protocol_v1")
      .a("abc", "value123")
      .a("def", "10.1")
      .e(e("inner"))
      .e(e("second").a("some", "value"))
      .asString()

    assertXmlElementsAreEqual(expectedXml, xml)
  }

  "it" should "construct root element with numeric attributes" in {
    val expectedXml = <document_root xmlns="urn:debugger_protocol_v1" abc="123" def="10.1"/>

    val xml = e("document_root", "urn:debugger_protocol_v1")
      .a("abc", 123)
      .a("def", 10.1)
      .asString()

    assertXmlElementsAreEqual(expectedXml, xml)
  }


  def assertXmlElementsAreEqual(expectedXml: Elem, actualXml: String): Unit = {
    val comparison = DiffBuilder.compare(Input.from(expectedXml.toString()))
      .withTest(Input.fromString(actualXml))
      .normalizeWhitespace()
      .build()
    assert(!comparison.hasDifferences, comparison.toString)
  }
}
