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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgumentConfiguration {
    private final Map<String, List<ArgumentFormat>> commands;

    private ArgumentConfiguration(Map<String, List<ArgumentFormat>> commands) {
        this.commands = commands;
    }

    public Arguments buildArgumentsFrom(String textToParse) {
        String[] commandParts = textToParse.split(" ");
        String command = commandParts[0];
        List<String> arguments = new ArrayList<>();
        for (int i = 1; i < commandParts.length; i++) {
            String argument = commandParts[i];
            arguments.add(argument);
        }
        return new Arguments(arguments, commands.get(command));
    }

    public static class Builder {
        private final Map<String, List<ArgumentFormat>> commands = new HashMap<>();
        private Builder() {}

        public static Builder configuration() {
            return new Builder();
        }

        public Builder withCommand(String command, ArgumentFormat... argumentFormats) {
            commands.put(command, Arrays.asList(argumentFormats));
            return this;
        }

        public ArgumentConfiguration build() {
            return new ArgumentConfiguration(commands);
        }
    }
}

