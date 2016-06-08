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

import java.util.function.Function;

enum ArgumentType {
    NUMERIC(Integer::valueOf), STRING(String::valueOf), BOOLEAN("1"::equals);

    private final Function<String, ?> conversion;

    ArgumentType(Function<String, ?> conversion) {
        this.conversion = conversion;
    }

    public <T> T getValue(String textRepresentation) {
        return (T) conversion.apply(textRepresentation);
    }
}
