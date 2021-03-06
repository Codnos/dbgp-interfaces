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

package com.codnos.dbgp.internal.messages;

import com.codnos.dbgp.internal.commands.breakpoint.BreakpointGetResponse;
import com.codnos.dbgp.internal.commands.breakpoint.BreakpointRemoveResponse;
import com.codnos.dbgp.internal.commands.breakpoint.BreakpointSetResponse;
import com.codnos.dbgp.internal.commands.breakpoint.BreakpointUpdateResponse;
import com.codnos.dbgp.internal.commands.context.ContextGetResponse;
import com.codnos.dbgp.internal.commands.eval.EvalResponse;
import com.codnos.dbgp.internal.commands.run.BreakNowResponse;
import com.codnos.dbgp.internal.commands.stack.StackDepthResponse;
import com.codnos.dbgp.internal.commands.stack.StackGetResponse;
import com.codnos.dbgp.internal.commands.status.StatusResponse;
import org.w3c.dom.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static com.codnos.dbgp.internal.xml.XmlUtil.parseMessage;
import static java.util.Arrays.asList;

public class MessageFactory {

    private static final List<Class<? extends XmlMessage>> messageResponseClasses = asList(InitMessage.class,
            BreakpointSetResponse.class,
            BreakpointGetResponse.class,
            BreakpointRemoveResponse.class,
            BreakpointUpdateResponse.class,
            StatusResponse.class,
            StackDepthResponse.class,
            StackGetResponse.class,
            ContextGetResponse.class,
            BreakNowResponse.class,
            EvalResponse.class
            );

    public static Message getMessage(String message) {
        Document parsedMessage = parseMessage(message);
        for (Class<? extends XmlMessage> messageResponseClass : messageResponseClasses) {
            if (canBuildFrom(messageResponseClass, parsedMessage)) {
                return newInstance(messageResponseClass, parsedMessage);
            }
        }
        return null;
    }

    private static XmlMessage newInstance(Class<? extends XmlMessage> messageResponseClass, Document parsedMessage) {
        try {
            Constructor<? extends XmlMessage> constructor = messageResponseClass.getConstructor(Document.class);
            return constructor.newInstance(parsedMessage);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean canBuildFrom(Class<? extends XmlMessage> messageResponseClass, Object parsedMessage) {
        try {
            Method m = messageResponseClass.getMethod("canBuildFrom", Document.class);
            return (Boolean) m.invoke(null, parsedMessage);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
