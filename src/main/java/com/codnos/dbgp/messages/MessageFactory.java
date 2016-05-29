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

package com.codnos.dbgp.messages;

import com.codnos.dbgp.commands.breakpoint.BreakpointSetResponse;
import com.codnos.dbgp.commands.context.ContextGetResponse;
import com.codnos.dbgp.commands.stack.StackDepthResponse;
import com.codnos.dbgp.commands.stack.StackGetResponse;
import com.codnos.dbgp.commands.status.StatusResponse;
import org.w3c.dom.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static com.codnos.dbgp.xml.XmlUtil.parseMessage;
import static java.util.Arrays.asList;

public class MessageFactory {

    private static final List<Class<? extends XmlMessage>> messageResponseClasses = asList(InitMessage.class,
            BreakpointSetResponse.class,
            StatusResponse.class,
            StackDepthResponse.class,
            StackGetResponse.class,
            ContextGetResponse.class);

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
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean canBuildFrom(Class<? extends XmlMessage> messageResponseClass, Object parsedMessage) {
        try {
            Method m = messageResponseClass.getMethod("canBuildFrom", Document.class);
            return (Boolean) m.invoke(null, parsedMessage);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
