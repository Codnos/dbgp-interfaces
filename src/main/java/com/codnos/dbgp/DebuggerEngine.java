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
import com.codnos.dbgp.commands.property.PropertyValue;
import com.codnos.dbgp.commands.stack.StackFrame;
import com.codnos.dbgp.commands.status.State;
import com.codnos.dbgp.commands.status.StateChangeHandler;

import java.util.Collection;

public interface DebuggerEngine {
    String getAppId();

    String getSession();

    String getIdeKey();

    String getLanguage();

    String getProtocolVersion();

    String getInitialFileUri();

    void run() throws Exception;

    void stepOver();

    Breakpoint breakpointSet(Breakpoint breakpoint);

    void registerStateChangeHandler(StateChangeHandler stateChangeHandler);

    int getStackDepth();

    StackFrame getFrame(int depth);

    Collection<PropertyValue> getVariables(int depth);

    State getState();
}
