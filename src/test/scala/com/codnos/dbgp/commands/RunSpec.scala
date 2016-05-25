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

package com.codnos.dbgp.commands

import com.codnos.dbgp.commands.status.StateChangeHandler
import org.mockito.Matchers._
import org.mockito.Mockito._

class RunSpec extends CommandSpec {

  "Command" should "have message constructed from the parameters" in {
    val command = new Run("456")

    command should have(
      'name ("run"),
      'message ("run -i 456"),
      'handlerKey ("status:456")
    )
  }

  "CommandHandler" should "register state change handler and step over" in {
    val handler = new Run.CommandHandler(engine)

    handler.channelRead(ctx, "run -i 456")

    verify(engine).registerStateChangeHandler(any(classOf[StateChangeHandler]))
    verify(engine).run()
  }
}