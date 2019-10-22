/**
 * Copyright (C) 2019 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
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
package nl.knaw.dans.narcis.graphql

import better.files.File
import better.files.File.root
import nl.knaw.dans.narcis.graphql.app.database.DatabaseConfiguration
import org.apache.commons.configuration.PropertiesConfiguration

import scala.concurrent.duration._
import scala.language.postfixOps

case class Configuration(version: String,
                         serverPort: Int,
                         profilingThreshold: FiniteDuration,
                         sysvsoiConfig: DatabaseConfiguration,
                        )

object Configuration {

  def apply(home: File): Configuration = {
    val cfgPath = Seq(
      root / "etc" / "opt" / "dans.knaw.nl" / "narcis-graphql",
      home / "cfg")
      .find(_.exists)
      .getOrElse { throw new IllegalStateException("No configuration directory found") }
    val properties = new PropertiesConfiguration() {
      setDelimiterParsingDisabled(true)
      load((cfgPath / "application.properties").toJava)
    }

    new Configuration(
      version = (home / "bin" / "version").contentAsString.stripLineEnd,
      serverPort = properties.getInt("daemon.http.port"),
      profilingThreshold = properties.getLong("graphql.profiling.threshold") milliseconds,
      sysvsoiConfig = DatabaseConfiguration(
        properties.getString("sysvsoi.database.driver-class"),
        properties.getString("sysvsoi.database.url"),
        properties.getString("sysvsoi.database.username"),
        properties.getString("sysvsoi.database.password"),
      ),
    )
  }
}
