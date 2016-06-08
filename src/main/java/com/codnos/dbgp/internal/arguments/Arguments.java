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

import java.util.List;

class Arguments {
    public static final String ARGUMENT_PREFIX = "-";
    private final List<String> arguments;
    private final List<ArgumentFormat> argumentFormats;

    public Arguments(List<String> arguments, List<ArgumentFormat> argumentFormats) {
        this.arguments = arguments;
        this.argumentFormats = argumentFormats;
    }

    public int getInteger(String argumentName) {
        return findValueFor(argumentName);
    }

    public String getString(String argumentName) {
        return findValueFor(argumentName);
    }

    public boolean getBoolean(String argumentName) {
        return findValueFor(argumentName);
    }

    private <T> T findValueFor(String argumentName) {
        for (ArgumentFormat argumentFormat : argumentFormats) {
            if (argumentFormat.getName().equals(argumentName)) {
                return getValueFor(argumentFormat);
            }
        }
        throw new ArgumentNotConfiguredException();
    }

    private <T> T getValueFor(ArgumentFormat argumentFormat) {
        for (int i = 0; i < arguments.size(); i++) {
            if ((ARGUMENT_PREFIX + argumentFormat.getName()).equals(arguments.get(i))) {
                if (arguments.size() < i + 2 || arguments.get(i + 1).startsWith(ARGUMENT_PREFIX)) {
                    throw new ArgumentValueNotAvailableException();
                } else {
                    return argumentFormat.getValueOfType(arguments.get(i + 1));
                }
            }
        }
        throw new ArgumentValueNotAvailableException();
    }
}
