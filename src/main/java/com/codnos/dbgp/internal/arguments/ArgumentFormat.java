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

package com.codnos.dbgp.internal.arguments;

public class ArgumentFormat {
    private final String name;
    private final ArgumentType argumentType;

    public static ArgumentFormat numeric(String name) {
        return new ArgumentFormat(name, ArgumentType.NUMERIC);
    }

    public static ArgumentFormat string(String name) {
        return new ArgumentFormat(name, ArgumentType.STRING);
    }

    public static ArgumentFormat bool(String name) {
        return new ArgumentFormat(name, ArgumentType.BOOLEAN);
    }

    private ArgumentFormat(String name, ArgumentType argumentType) {
        this.name = name;
        this.argumentType = argumentType;
    }

    public String getName() {
        return name;
    }

    public <T> T getValueOfType(String textRepresentation) {
        return argumentType.getValue(textRepresentation);
    }
}
