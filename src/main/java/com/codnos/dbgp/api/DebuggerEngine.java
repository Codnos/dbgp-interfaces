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

import java.util.Collection;
import java.util.Optional;

public interface DebuggerEngine {
    String getAppId();

    String getSession();

    String getIdeKey();

    String getLanguage();

    String getProtocolVersion();

    String getInitialFileUri();

    void run() throws Exception;

    void stepOver();

    void stepInto();

    void stepOut();

    Breakpoint breakpointSet(Breakpoint breakpoint);

    Optional<Breakpoint> breakpointRemove(String breakpointId);

    Breakpoint breakpointGet(String breakpointId);

    void breakpointUpdate(String breakpointId, BreakpointUpdateData breakpointUpdateData);

    void registerStatusChangeHandler(StatusChangeHandler statusChangeHandler);

    int getStackDepth();

    StackFrame getFrame(int depth);

    Collection<PropertyValue> getVariables(int depth);

    Status getStatus();

    boolean breakNow();

    Optional<PropertyValue> eval(int depth, String expression);
}
