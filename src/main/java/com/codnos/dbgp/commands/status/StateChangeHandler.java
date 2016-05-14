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

package com.codnos.dbgp.commands.status;

public interface StateChangeHandler {
    /**
     * Method that should handle (or just be called) whenever the state changes
     * and this handler's applicableFor method retrurned true.
     * @param previousState
     * @param currentState
     */
    void stateChanged(State previousState, State currentState);

    /**
     * Should this handler's stateChanged method be called for given combination of previous and new statuses.
     * @param previous previous state
     * @param current current (new) state
     * @return true if should be called, false otherwise
     */
    boolean applicableFor(State previous, State current);
}
