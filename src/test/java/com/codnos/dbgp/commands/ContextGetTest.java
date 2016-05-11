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

package com.codnos.dbgp.commands;

import com.codnos.dbgp.commands.context.ContextGet;
import com.codnos.dbgp.commands.property.PropertyValue;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static com.codnos.dbgp.xml.XmlUtil.parseMessage;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ContextGetTest {

    @Test
    public void shouldExtractAllTheDataFromTheMessage() {
        String message = "<response xmlns=\"urn:debugger_protocol_v1\" xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\" command=\"context_get\"\n" +
                "          context=\"context_id\"\n" +
                "          transaction_id=\"transaction_id\">\n" +
                "    <property\n" +
                "    name=\"short_name\"\n" +
                "    fullname=\"long_name\"\n" +
                "    type=\"data_type\"\n" +
                "    classname=\"name_of_object_class\"\n" +
                "    constant=\"0\"\n" +
                "    children=\"0\"\n" +
                "    encoding=\"none\"\n" +
                "    >\n" +
                "...encoded Value Data...\n" +
                "</property>\n" +
                "</response>";

        ContextGet.Response response = new ContextGet.Response(parseMessage(message));

        Collection<PropertyValue> variables = response.getVariables();
        Iterator<PropertyValue> iterator = variables.iterator();
        PropertyValue propertyValue = iterator.next();
        assertThat(propertyValue.getName(), is("short_name"));
        assertThat(propertyValue.getType(), is("data_type"));
        assertThat(propertyValue.getValue(), is("\n...encoded Value Data...\n"));
    }
}
