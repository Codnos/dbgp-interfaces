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

package com.codnos.dbgp.api;

import com.codnos.dbgp.commands.Init;
import com.codnos.dbgp.commands.breakpoint.BreakpointSetCommandHandler;
import com.codnos.dbgp.commands.context.ContextGetCommandHandler;
import com.codnos.dbgp.commands.run.RunCommandHandler;
import com.codnos.dbgp.commands.stack.StackDepthCommandHandler;
import com.codnos.dbgp.commands.stack.StackGetCommandHandler;
import com.codnos.dbgp.commands.status.StatusChangeHandlerFactory;
import com.codnos.dbgp.commands.status.StatusCommandHandler;
import com.codnos.dbgp.commands.step.StepOverCommandHandler;
import com.codnos.dbgp.handlers.DBGPInitHandler;
import com.codnos.dbgp.handlers.DBGpCommandDecoder;
import com.codnos.dbgp.handlers.DBGpResponseEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class DBGpEngine {
    private final int port;
    private final DebuggerEngine debuggerEngine;
    private final StatusChangeHandlerFactory statusChangeHandlerFactory;
    private EventLoopGroup workerGroup;

    public DBGpEngine(int port, DebuggerEngine debuggerEngine, StatusChangeHandlerFactory statusChangeHandlerFactory) {
        this.port = port;
        this.debuggerEngine = debuggerEngine;
        this.statusChangeHandlerFactory = statusChangeHandlerFactory;
    }

    public void connect() throws InterruptedException {
        workerGroup = new NioEventLoopGroup();
        connectToIde(workerGroup);
    }

    private void connectToIde(EventLoopGroup workerGroup) throws InterruptedException {
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new DBGPInitHandler(new Init(debuggerEngine)),
                        new DBGpCommandDecoder(),
                        new DBGpResponseEncoder(),
                        new BreakpointSetCommandHandler(debuggerEngine),
                        new StackDepthCommandHandler(debuggerEngine),
                        new RunCommandHandler(debuggerEngine, statusChangeHandlerFactory),
                        new StepOverCommandHandler(debuggerEngine, statusChangeHandlerFactory),
                        new StackGetCommandHandler(debuggerEngine),
                        new ContextGetCommandHandler(debuggerEngine),
                        new StatusCommandHandler(debuggerEngine)
                );
            }
        });
        b.connect("localhost", port).sync();
    }

    public void disconnect() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
