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

public class BreakpointUpdateData {
    private final Optional<Boolean> enabled;

    public BreakpointUpdateData(boolean enabled) {
        this.enabled = Optional.of(enabled);
    }

    public boolean hasState() {
        return enabled.isPresent();
    }

    public boolean isEnabled() {
        return enabled.isPresent() && enabled.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BreakpointUpdateData that = (BreakpointUpdateData) o;

        return enabled.equals(that.enabled);
    }

    @Override
    public int hashCode() {
        return enabled.hashCode();
    }
}
