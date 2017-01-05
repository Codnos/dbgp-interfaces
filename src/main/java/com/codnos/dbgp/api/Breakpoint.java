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

import java.util.Optional;

public class Breakpoint {
    private final String breakpointId;
    private final boolean enabled;
    private final BreakpointType type;
    private final Optional<String> fileURL;
    private final Optional<Integer> lineNumber;
    private final Optional<String> function;
    private final Optional<String> exception;
    private final Optional<String> expression;
    private final Optional<String> hitValue;
    private final Optional<String> hitCondition;
    private final Optional<Integer> hitCount;


    public Breakpoint(String fileURL, int lineNumber) {
        this.fileURL = Optional.of(fileURL);
        this.lineNumber = Optional.of(lineNumber);
        this.breakpointId = null;
        this.enabled = true;
        this.type = BreakpointType.LINE;
        this.function = Optional.empty();
        this.exception = Optional.empty();
        this.expression = Optional.empty();
        this.hitValue = Optional.empty();
        this.hitCondition = Optional.empty();
        this.hitCount = Optional.empty();
    }

    public Breakpoint(String breakpointId, boolean enabled, String type, Optional<String> fileURL, Optional<Integer> lineNumber, Optional<String> function, Optional<String> exception, Optional<String> expression, Optional<String> hitValue, Optional<String> hitCondition, Optional<Integer> hitCount) {
        this.breakpointId = breakpointId;
        this.enabled = enabled;
        this.type = BreakpointType.valueOf(type.toUpperCase());
        this.fileURL = fileURL;
        this.lineNumber = lineNumber;
        this.function = function;
        this.exception = exception;
        this.expression = expression;
        this.hitValue = hitValue;
        this.hitCondition = hitCondition;
        this.hitCount = hitCount;
    }

    public Breakpoint(Breakpoint breakpoint, String breakpointId) {
        this.breakpointId = breakpointId;
        this.enabled = breakpoint.enabled;
        this.type = breakpoint.type;
        this.fileURL = breakpoint.fileURL;
        this.lineNumber = breakpoint.lineNumber;
        this.function = breakpoint.function;
        this.exception = breakpoint.exception;
        this.expression = breakpoint.expression;
        this.hitValue = breakpoint.hitValue;
        this.hitCondition = breakpoint.hitCondition;
        this.hitCount = breakpoint.hitCount;
    }

    public BreakpointType getType() {
        return type;
    }

    public Optional<String> getFileURL() {
        return fileURL;
    }

    public Optional<Integer> getLineNumber() {
        return lineNumber;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getBreakpointId() {
        return breakpointId;
    }

    public Optional<String> getFunction() {
        return function;
    }

    public Optional<String> getException() {
        return exception;
    }

    public Optional<String> getExpression() {
        return expression;
    }

    public Optional<String> getHitValue() {
        return hitValue;
    }

    public Optional<String> getHitCondition() {
        return hitCondition;
    }

    public Optional<Integer> getHitCount() {
        return hitCount;
    }
}