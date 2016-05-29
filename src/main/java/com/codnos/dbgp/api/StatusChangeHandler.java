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

public interface StatusChangeHandler {
    /**
     * Method that should handle (or just be called) whenever the status changes
     * and this handler's applicableFor method returned true.
     * @param previous
     * @param current
     */
    void statusChanged(Status previous, Status current);

    /**
     * Should this handler's statusChanged method be called for given combination of previous and new statuses.
     * @param previous previous status
     * @param current current (new) status
     * @return true if should be called, false otherwise
     */
    boolean applicableFor(Status previous, Status current);
}
