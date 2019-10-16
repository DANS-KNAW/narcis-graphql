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
package nl.knaw.dans.narcis.graphql.app.repository.vsoi_impl

import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import nl.knaw.dans.narcis.graphql.app.model.{InputPerson, Person, PersonId}
import nl.knaw.dans.narcis.graphql.app.repository.PersonDao
import org.joda.time.LocalDate

import scala.collection.immutable.Stream.Empty

class VsoiPersonDao  extends PersonDao with DebugEnhancedLogging {
  override def getAll: Seq[Person] = ???

  override def find(id: PersonId): Option[Person] = {
    trace(id)
    logger.info("Fixed to Alice!")
    // fixed fake, always return Alice!
    Some(Person(id, "Alice", new LocalDate(1990, 1, 1), "London"))
  }

  override def find(ids: Seq[PersonId]): Seq[Person] = {
    trace(ids)
    logger.info("requesting persons with ids")
    find(ids.head).toList
    // nothing yet!
    //Seq.empty[Person]
  }

  override def store(person: InputPerson): Person = ???
}
