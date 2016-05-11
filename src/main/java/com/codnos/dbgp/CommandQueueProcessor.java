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
import com.codnos.dbgp.handlers.DBGpServerToClientConnectionHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandQueueProcessor {

    private final DBGpServerToClientConnectionHandler outboundConnectionHandler;
    private final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<Command>();
    private final Thread senderThread = new Thread(new CommandSender());
    private boolean stop = false;

    public CommandQueueProcessor(DBGpServerToClientConnectionHandler outboundConnectionHandler) {
        this.outboundConnectionHandler = outboundConnectionHandler;
    }

    public void add(Command command) {
        commandQueue.add(command);
    }

    public void start() {
        senderThread.start();
    }

    public void stop() {
        stop = true;
    }

    private class CommandSender implements Runnable {
        @Override
        public void run() {
            do {
                try {
                    System.out.println("waiting to take message from queue");
                    Command command = commandQueue.take();
                    System.out.println("got message from queue = " + command.getHandlerKey());
                    outboundConnectionHandler.writeAndFlush(command);
                    System.out.println("message from queue sent = " + command.getHandlerKey());
                } catch (InterruptedException e) {
                    stop = true;
                }
            } while (!stop);
        }
    }
}
