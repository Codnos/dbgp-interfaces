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

import com.codnos.dbgp.api.DebuggerIde;
import com.codnos.dbgp.commands.breakpoint.BreakpointSet;
import com.codnos.dbgp.api.StatusValue;
import com.codnos.dbgp.handlers.DBGpResponseDecoder;
import com.codnos.dbgp.commands.Command;
import com.codnos.dbgp.commands.Run;
import com.codnos.dbgp.api.Breakpoint;
import com.codnos.dbgp.api.Context;
import com.codnos.dbgp.commands.context.ContextGet;
import com.codnos.dbgp.commands.stack.StackDepth;
import com.codnos.dbgp.api.StackFrame;
import com.codnos.dbgp.commands.stack.StackGet;
import com.codnos.dbgp.commands.status.Status;
import com.codnos.dbgp.commands.step.StepOver;
import com.codnos.dbgp.handlers.DBGpCommandEncoder;
import com.codnos.dbgp.messages.InitMessage;
import com.codnos.dbgp.handlers.DBGpResponseHandler;
import com.codnos.dbgp.handlers.DBGpServerToClientConnectionHandler;
import com.codnos.dbgp.handlers.MessageHandler;
import com.codnos.dbgp.messages.Message;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.atomic.AtomicInteger;

public class DBGpIde {
    private final int port;
    private final DBGpEventsHandler eventsHandler;
    private final AtomicInteger transactionId = new AtomicInteger();
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private DBGpServerToClientConnectionHandler outboundConnectionHandler = new DBGpServerToClientConnectionHandler();
    private DebuggerIde debuggerIde;
    private CommandQueueProcessor commandQueueProcessor = new CommandQueueProcessor(outboundConnectionHandler);

    public DBGpIde(int port, DBGpEventsHandler eventsHandler) {
        this.port = port;
        this.eventsHandler = eventsHandler;
    }

    public void startListening() throws InterruptedException {
        registerInitHandler();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(outboundConnectionHandler,
                                new DBGpCommandEncoder(),
                                new DBGpResponseDecoder(),
                                new DBGpResponseHandler(eventsHandler));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture channelFuture = b.bind(port).sync();
        if (channelFuture.isDone() && channelFuture.isSuccess()) {
            System.out.println("Successfully connected");
        } else if (channelFuture.isCancelled()) {
            System.out.println("Connection cancelled");
        } else if (!channelFuture.isSuccess()) {
            System.out.println("Failed to connect");
            channelFuture.cause().printStackTrace();
        }
    }

    public void stopListening() {
        System.out.println("Switching off");
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        commandQueueProcessor.stop();
    }

    public Breakpoint breakpointSet(final Breakpoint breakpoint) {
        String transactionId = nextTransaction();
        BreakpointSet command = new BreakpointSet(transactionId, breakpoint);
        sendCommand(command);
        BreakpointSet.Response response = eventsHandler.getResponse(command);
        return new Breakpoint(breakpoint, response.getBreakpointId(), response.getState());
    }

    public void run() {
        String transactionId = nextTransaction();
        Run command = new Run(transactionId);
        eventsHandler.registerMessageHandler(command,
                new MessageHandler() {
                    public void handle(Message message) {
                        Status.Response response = (Status.Response) message;
                        debuggerIde.onStatus(response.getStatus());
                    }
                }
        );
        sendCommand(command);
    }

    public void stepOver() {
        String transactionId = nextTransaction();
        StepOver command = new StepOver(transactionId);
        eventsHandler.registerMessageHandler(command,
                new MessageHandler() {
                    public void handle(Message message) {
                        Status.Response response = (Status.Response) message;
                        debuggerIde.onStatus(response.getStatus());
                    }
                }
        );
        sendCommand(command);
    }

    public int stackDepth() {
        String transactionId = nextTransaction();
        StackDepth command = new StackDepth(transactionId);
        sendCommand(command);
        StackDepth.Response response = eventsHandler.getResponse(command);
        return response.getDepth();
    }

    public Context contextGet(int stackDepth) {
        String transactionId = nextTransaction();
        ContextGet command = new ContextGet(transactionId, stackDepth);
        sendCommand(command);
        ContextGet.Response response = eventsHandler.getResponse(command);
        return new Context(response.getVariables());
    }

    public StackFrame stackGet(int depth) {
        String transactionId = nextTransaction();
        StackGet command = new StackGet(transactionId, depth);
        sendCommand(command);
        StackGet.Response response = eventsHandler.getResponse(command);
        return new StackFrame(response.getFileUrl(), response.getLineNumber(), response.getWhere());
    }

    public StatusValue status() {
        String transactionId = nextTransaction();
        Status command = new Status(transactionId);
        sendCommand(command);
        Status.Response response = eventsHandler.getResponse(command);
        return response.getStatus();
    }

    private String nextTransaction() {
        return Integer.toString(transactionId.incrementAndGet());
    }

    public void registerIde(DebuggerIde debuggerIde) {
        this.debuggerIde = debuggerIde;
    }

    private void sendCommand(Command command) {
        commandQueueProcessor.add(command);
    }

    private void registerInitHandler() {
        eventsHandler.registerMessageHandler("init", "init", new MessageHandler() {
            @Override
            public void handle(Message message) {
                try {
                    commandQueueProcessor.start();
                    debuggerIde.onConnected(((InitMessage) message).toSystemInfo());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
