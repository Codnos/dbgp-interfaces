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
import com.codnos.dbgp.internal.commands.run.{RunCommand, RunCommandHandler}
import com.codnos.dbgp.internal.impl.StatusChangeHandlerFactory
import org.mockito.Matchers._
import org.mockito.Mockito._

class RunSpec extends CommandSpec {

  val argumentConfiguration = configuration.withCommand("run", numeric("i")).build

  "Command" should "have message constructed from the parameters" in {
    val command = new RunCommand("456")

    command should have(
      'name ("run"),
      'message ("run -i 456"),
      'handlerKey ("status:456")
    )
  }

  "CommandHandler" should "register status change handler and run" in {
    val handler = new RunCommandHandler(engine, new StatusChangeHandlerFactory, argumentConfiguration)

    handler.channelRead(ctx, "run -i 456")

    verify(engine).registerStatusChangeHandler(any(classOf[StatusChangeHandler]))
    verify(engine).run()
  }
}
