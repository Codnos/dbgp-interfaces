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

public class Breakpoint {
    private final String fileURL;
    private final int lineNumber;
    private final String breakpointId;
    private final String state;

    public Breakpoint(String fileURL, int lineNumber) {
        this.fileURL = fileURL;
        this.lineNumber = lineNumber;
        this.breakpointId = null;
        this.state = null;
    }

    public Breakpoint(Breakpoint breakpoint, String breakpointId, String state) {
        this.fileURL = breakpoint.fileURL;
        this.lineNumber = breakpoint.lineNumber;
        this.breakpointId = breakpointId;
        this.state = state;
    }

    public String getFileURL() {
        return fileURL;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getState() {
        return state;
    }

    public String getBreakpointId() {
        return breakpointId;
    }
}