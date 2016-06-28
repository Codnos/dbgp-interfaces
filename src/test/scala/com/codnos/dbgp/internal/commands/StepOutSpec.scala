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

package com.codnos.dbgp.internal.commands

import com.codnos.dbgp.api.StatusChangeHandler
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration.Builder._
import com.codnos.dbgp.internal.arguments.ArgumentFormat._
import com.codnos.dbgp.internal.commands.step.{StepOutCommand, StepOutCommandHandler}
import com.codnos.dbgp.internal.impl.StatusChangeHandlerFactory
import org.mockito.Matchers.any
import org.mockito.Mockito.verify

class StepOutSpec extends CommandSpec {

  val argumentConfiguration = configuration.withCommand("step_out", numeric("i")).build

  "Command" should "have message constructed from the parameters" in {
    val command = new StepOutCommand("456")

    command should have(
      'name ("step_out"),
      'message ("step_out -i 456"),
      'handlerKey ("status:456")
    )
  }

  "CommandHandler" should "register status change handler and step out" in {
    val handler = new StepOutCommandHandler(engine, new StatusChangeHandlerFactory, argumentConfiguration)

    handler.channelRead(ctx, "step_out -i 456")

    verify(engine).registerStatusChangeHandler(any(classOf[StatusChangeHandler]))
    verify(engine).stepOut()
  }
}
