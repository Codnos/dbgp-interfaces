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

import com.codnos.dbgp.api.*;
import com.codnos.dbgp.commands.Command;
import com.codnos.dbgp.commands.run.RunCommand;
import com.codnos.dbgp.commands.breakpoint.BreakpointSetCommand;
import com.codnos.dbgp.commands.breakpoint.BreakpointSetResponse;
import com.codnos.dbgp.commands.context.ContextGetCommand;
import com.codnos.dbgp.commands.context.ContextGetResponse;
import com.codnos.dbgp.commands.stack.StackDepthCommand;
import com.codnos.dbgp.commands.stack.StackDepthResponse;
import com.codnos.dbgp.commands.stack.StackGetCommand;
import com.codnos.dbgp.commands.stack.StackGetResponse;
import com.codnos.dbgp.commands.status.StatusCommand;
import com.codnos.dbgp.commands.status.StatusResponse;
import com.codnos.dbgp.commands.step.StepOverCommand;
import com.codnos.dbgp.handlers.*;
import com.codnos.dbgp.messages.InitMessage;
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

    public DBGpIde(int port, DBGpEventsHandler eventsHandler, DebuggerIde debuggerIde) {
        this.port = port;
        this.eventsHandler = eventsHandler;
        this.debuggerIde = debuggerIde;
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
        BreakpointSetCommand command = new BreakpointSetCommand(transactionId, breakpoint);
        sendCommand(command);
        BreakpointSetResponse response = eventsHandler.getResponse(command);
        return new Breakpoint(breakpoint, response.getBreakpointId(), response.getState());
    }

    public void run() {
        String transactionId = nextTransaction();
        RunCommand command = new RunCommand(transactionId);
        eventsHandler.registerMessageHandler(command,
                new MessageHandler() {
                    public void handle(Message message) {
                        StatusResponse response = (StatusResponse) message;
                        debuggerIde.onStatus(response.getStatus(), DBGpIde.this);
                    }
                }
        );
        sendCommand(command);
    }

    public void stepOver() {
        String transactionId = nextTransaction();
        StepOverCommand command = new StepOverCommand(transactionId);
        eventsHandler.registerMessageHandler(command,
                new MessageHandler() {
                    public void handle(Message message) {
                        StatusResponse response = (StatusResponse) message;
                        debuggerIde.onStatus(response.getStatus(), DBGpIde.this);
                    }
                }
        );
        sendCommand(command);
    }

    public int stackDepth() {
        String transactionId = nextTransaction();
        StackDepthCommand command = new StackDepthCommand(transactionId);
        sendCommand(command);
        StackDepthResponse response = eventsHandler.getResponse(command);
        return response.getDepth();
    }

    public Context contextGet(int stackDepth) {
        String transactionId = nextTransaction();
        ContextGetCommand command = new ContextGetCommand(transactionId, stackDepth);
        sendCommand(command);
        ContextGetResponse response = eventsHandler.getResponse(command);
        return new Context(response.getVariables());
    }

    public StackFrame stackGet(int depth) {
        String transactionId = nextTransaction();
        StackGetCommand command = new StackGetCommand(transactionId, depth);
        sendCommand(command);
        StackGetResponse response = eventsHandler.getResponse(command);
        return new StackFrame(response.getFileUrl(), response.getLineNumber(), response.getWhere());
    }

    public Status status() {
        String transactionId = nextTransaction();
        StatusCommand command = new StatusCommand(transactionId);
        sendCommand(command);
        StatusResponse response = eventsHandler.getResponse(command);
        return response.getStatus();
    }

    private String nextTransaction() {
        return Integer.toString(transactionId.incrementAndGet());
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
