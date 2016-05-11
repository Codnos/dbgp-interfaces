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

import com.codnos.dbgp.commands.breakpoint.Breakpoint;
import com.codnos.dbgp.messages.Init;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.awaitility.Duration.FIVE_SECONDS;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DBGpFeatureTest {

    private FakeInitDebuggerIde debuggerIde;
    private DebuggerEngine debuggerEngine;
    private DBGpEventsHandler eventsHandler = new DBGpEventsHandler();

    @Before
    public void setUp() throws Exception {
        debuggerIde = new FakeInitDebuggerIde();
        debuggerEngine = mock(DebuggerEngine.class);
    }

    @Test
    public void shouldReceiveInitMessageAfterStartingUp() throws InterruptedException {
        DBGpIde ide = new DBGpIde(9000, eventsHandler);
        ide.registerIde(debuggerIde);
        DBGpEngine engine = new DBGpEngine(9000, debuggerEngine);
        ide.startListening();
        engine.connect();

        try {
            await().atMost(FIVE_SECONDS).until(initMessage(), notNullValue());
        } finally {
            engine.disconnect();
            ide.stopListening();
        }
    }

    @Test
    public void shouldSendBreakpointsAfterGettingConnected() throws InterruptedException {
        DBGpIde ide = new DBGpIde(9000, eventsHandler);
        ide.registerIde(debuggerIde);
        DBGpEngine engine = new DBGpEngine(9000, debuggerEngine);
        ide.startListening();
        engine.connect();

        try {
            ide.breakpointSet(new Breakpoint("file", 123));
            await().atMost(FIVE_SECONDS).until(new Runnable() {
                @Override
                public void run() {
                    verify(debuggerEngine).breakpointSet(any(Breakpoint.class));
                }
            });
        } finally {
            engine.disconnect();
            ide.stopListening();
        }
    }

    private Callable<Init> initMessage() {
        return new Callable<Init>() {
            @Override
            public Init call() throws Exception {
                return debuggerIde.getInitMessage();
            }
        };
    }

    private class FakeInitDebuggerIde extends StubDebuggerIde {
        private Init message;

        public Init getInitMessage() {
            return message;
        }

        @Override
        public void onConnected(Init message) {
            this.message = message;
        }
    }
}
