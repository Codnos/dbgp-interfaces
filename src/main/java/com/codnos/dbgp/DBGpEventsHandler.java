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

package com.codnos.dbgp;

import com.codnos.dbgp.commands.Command;
import com.codnos.dbgp.handlers.MessageHandler;
import com.codnos.dbgp.messages.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DBGpEventsHandler {
    private final Map<String, MessageHandler> responseHandlers = new HashMap<String, MessageHandler>();
    private final Map<String, Message> messageMap = new ConcurrentHashMap<String, Message>();
    private final Object monitor = new Object();

    public void receive(Message message) {
        MessageHandler messageHandler = responseHandlers.remove(message.getHandlerKey());
        if (messageHandler != null) {
            messageHandler.handle(message);
        } else {
            synchronized (monitor) {
                System.out.println(Thread.currentThread().getName() + ": got message with key: " + message.getHandlerKey());
                messageMap.put(message.getHandlerKey(), message);
                System.out.println(Thread.currentThread().getName() + ": notifying about message with key: " + message.getHandlerKey());
                monitor.notifyAll();
                System.out.println(Thread.currentThread().getName() + ": notified about message with key: " + message.getHandlerKey());
            }
        }
    }

    public void clearHandlers() {
        responseHandlers.clear();
    }

    public void registerMessageHandler(Command command, MessageHandler messageHandler) {
        responseHandlers.put(command.getHandlerKey(), messageHandler);
    }

    public void registerMessageHandler(String name, String transactionId, MessageHandler messageHandler) {
        responseHandlers.put(name + ":" + transactionId, messageHandler);
    }

    public <T> T getResponse(Command<T> command) {
        int tryCount = 0;
        System.out.println(Thread.currentThread().getName() + ": checking response for message with key: " + command.getHandlerKey());
        synchronized (monitor) {
            while (!messageMap.containsKey(command.getHandlerKey())) {

                System.out.println(Thread.currentThread().getName() + ": about to wait for message with key: " + command.getHandlerKey());
                try {
                    monitor.wait(3000L);
                    System.out.println(Thread.currentThread().getName() + ": got notified while waiting for message with key: " + command.getHandlerKey());
                    tryCount++;
                    if (tryCount > 10)
                        throw new RuntimeException("not waiting any longer!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            System.out.println(Thread.currentThread().getName() + ": no longer waiting for message with key: " + command.getHandlerKey());
            T result = (T) messageMap.remove(command.getHandlerKey());
            System.out.println(Thread.currentThread().getName() + ": after waiting for message with key: " + command.getHandlerKey() + " got response " + result + " with key " + (result != null ? ((Message) result).getHandlerKey() : null));
            return result;

        }
    }
}
