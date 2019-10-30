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
package nl.knaw.dans.narcis.graphql.app.repository.narcis_impl

import nl.knaw.dans.narcis.graphql.app.model.{ExternalPersonId, InputPerson, Person, PersonId}
import nl.knaw.dans.narcis.graphql.app.repository.PersonDao
import nl.knaw.dans.narcis.graphql.app.repository.demo_impl.DemoPersonDao
import nl.knaw.dans.narcis.graphql.app.repository.pigraph_impl.PidGraphpersonDao
import nl.knaw.dans.narcis.graphql.app.repository.vsoi_impl.VsoiPersonDao

class NarcisPersonDao(vsoi: VsoiPersonDao,
                      pidgraph: PidGraphpersonDao
                     ) extends PersonDao {

  // merge information, but start with VSOI and add only complementary non conflicting info from other sources.

  override def getAll: Seq[Person] = ???

  override def find(id: PersonId): Option[Person] = vsoi.find(id)

  override def find(ids: Seq[PersonId]): Seq[Person] = vsoi.find(ids)

  override def getExtIds(id: PersonId): Seq[ExternalPersonId] = {
    val extIdsFromVsoi = vsoi.getExtIds(id)
    val extIdsFromPidgraph = pidgraph.getExtIds(id)

    // merge them, plus deduplicate... only works if normalised in the same way
    (extIdsFromVsoi.toSet ++ extIdsFromPidgraph.toSet).toSeq
  }

  override def store(person: InputPerson): Person = ???
}
