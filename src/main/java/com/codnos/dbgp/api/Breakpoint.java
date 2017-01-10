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
    private final boolean temporary;
    private final BreakpointType type;
    private final Optional<String> fileURL;
    private final Optional<Integer> lineNumber;
    private final Optional<String> function;
    private final Optional<String> exception;
    private final Optional<String> expression;
    private final Optional<String> hitValue;
    private final Optional<String> hitCondition;
    private final Optional<Integer> hitCount;

    private Breakpoint(BreakpointBuilder builder) {
        this.breakpointId = builder.breakpointId;
        this.enabled = builder.enabled;
        this.temporary = builder.temporary;
        this.type = builder.type;
        this.fileURL = builder.fileURL;
        this.lineNumber = builder.lineNumber;
        this.function = builder.function;
        this.exception = builder.exception;
        this.expression = builder.expression;
        this.hitValue = builder.hitValue;
        this.hitCondition = builder.hitCondition;
        this.hitCount = builder.hitCount;
    }


    public static BreakpointBuilder aBreakpoint() {
        return new BreakpointBuilder();
    }

    public static BreakpointBuilder aCopyOf(Breakpoint breakpoint) {
        return new BreakpointBuilder(breakpoint);
    }

    public static BreakpointBuilder aLineBreakpoint(String fileUri, int lineNumber) {
        return aBreakpoint().withFileUri(fileUri).withLineNumber(lineNumber);
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

    public boolean isTemporary() {
        return temporary;
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

    public Breakpoint update(BreakpointUpdateData breakpointUpdateData) {
        BreakpointBuilder breakpointBuilder = aCopyOf(this);
        if (breakpointUpdateData.hasState()) {
            breakpointBuilder.withEnabled(breakpointUpdateData.isEnabled());
        }
        return breakpointBuilder.build();
    }

    public static class BreakpointBuilder {
        private String breakpointId;
        private boolean enabled = true;
        private boolean temporary = false;
        private BreakpointType type = BreakpointType.LINE;
        private Optional<String> fileURL;
        private Optional<Integer> lineNumber;
        private Optional<String> function;
        private Optional<String> exception;
        private Optional<String> expression;
        private Optional<String> hitValue;
        private Optional<String> hitCondition;
        private Optional<Integer> hitCount;

        BreakpointBuilder() {
        }

        BreakpointBuilder(Breakpoint breakpoint) {
            this.breakpointId = breakpoint.breakpointId;
            this.enabled = breakpoint.enabled;
            this.temporary = breakpoint.temporary;
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

        public Breakpoint build() {
            return new Breakpoint(this);
        }

        public BreakpointBuilder withFileUri(String fileUri) {
            this.fileURL = Optional.of(fileUri);
            return this;
        }

        public BreakpointBuilder withFileUri(Optional<String> fileUri) {
            this.fileURL = fileUri;
            return this;
        }

        public BreakpointBuilder withLineNumber(int lineNumber) {
            this.lineNumber = Optional.of(lineNumber);
            return this;
        }

        public BreakpointBuilder withLineNumber(Optional<Integer> lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public BreakpointBuilder withEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public BreakpointBuilder withBreakpointId(String breakpointId) {
            this.breakpointId = breakpointId;
            return this;
        }

        public BreakpointBuilder withTemporary(boolean temporary) {
            this.temporary = temporary;
            return this;
        }

        public BreakpointBuilder withType(String type) {
            this.type = BreakpointType.valueOf(type.toUpperCase());
            return this;
        }

        public BreakpointBuilder withFunction(Optional<String> function) {
            this.function = function;
            return this;
        }

        public BreakpointBuilder withException(Optional<String> exception) {
            this.exception = exception;
            return this;
        }

        public BreakpointBuilder withExpression(Optional<String> expression) {
            this.expression = expression;
            return this;
        }

        public BreakpointBuilder withHitValue(Optional<String> hitValue) {
            this.hitValue = hitValue;
            return this;
        }

        public BreakpointBuilder withHitCondition(Optional<String> hitCondition) {
            this.hitCondition = hitCondition;
            return this;
        }

        public BreakpointBuilder withHitCount(Optional<Integer> hitCount) {
            this.hitCount = hitCount;
            return this;
        }
    }
}