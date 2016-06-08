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

package com.codnos.dbgp.internal.arguments

import com.codnos.dbgp.UnitSpec
import com.codnos.dbgp.internal.arguments.ArgumentConfiguration.Builder.configuration
import com.codnos.dbgp.internal.arguments.ArgumentFormat.{bool, numeric, string}

class ArgumentsSpec extends UnitSpec {

  "arguments" should "return integer value for numeric argument" in {
    val argumentsConfig = configuration().withCommand("run", numeric("i")).build()

    val arguments = argumentsConfig.buildArgumentsFrom("run -i 123")

    arguments.getInteger("i") shouldBe 123
  }

  they should "work for more than one argument" in {
    val argumentsConfig = configuration().withCommand("stack_depth", numeric("d"), numeric("i")).build()

    val arguments = argumentsConfig.buildArgumentsFrom("stack_depth -i 123 -d 6")

    arguments.getInteger("i") shouldBe 123
    arguments.getInteger("d") shouldBe 6
  }

  they should "throw an exception if asked for argument that is not available" in {
    val argumentsConfig = configuration().withCommand("run", numeric("i")).build()

    val arguments = argumentsConfig.buildArgumentsFrom("run")

    intercept[ArgumentValueNotAvailableException] {
      arguments.getInteger("i")
    }
  }

  they should "throw an exception if asked for argument that is available but was not configured" in {
    val argumentsConfig = configuration().withCommand("run", numeric("i")).build()

    val arguments = argumentsConfig.buildArgumentsFrom("run -i 123 -d 6")

    intercept[ArgumentNotConfiguredException] {
      arguments.getInteger("d")
    }
  }

  they should "return string for string argument" in {
    val argumentsConfig = configuration().withCommand("feature_get", numeric("i"), string("n")).build()

    val arguments = argumentsConfig.buildArgumentsFrom("feature_get -i 123 -n encoding")

    arguments.getString("n") shouldBe "encoding"
  }

  they should "return boolean for boolean argument" in {
    val argumentsConfig = configuration().withCommand("breakpoint_set", numeric("i"), string("t"), bool("r"), string("x")).build()

    val arguments = argumentsConfig.buildArgumentsFrom("breakpoint_set -i 123 -t exception -r 1 -x NullPointerException")

    arguments.getBoolean("r") shouldBe true
  }

  they should "throw exception if argument in command is followed by another argument instead of value" in {
    val argumentsConfig = configuration().withCommand("stack_depth", numeric("d"), numeric("i")).build()

    val arguments = argumentsConfig.buildArgumentsFrom("stack_depth -i -d 6")

    intercept[ArgumentValueNotAvailableException] {
      arguments.getInteger("i")
    }
  }

  they should "throw exception if argument in command misses value" in {
    val argumentsConfig = configuration().withCommand("stack_depth", numeric("d"), numeric("i")).build()

    val arguments = argumentsConfig.buildArgumentsFrom("stack_depth -i")

    intercept[ArgumentValueNotAvailableException] {
      arguments.getInteger("i")
    }
  }
}
