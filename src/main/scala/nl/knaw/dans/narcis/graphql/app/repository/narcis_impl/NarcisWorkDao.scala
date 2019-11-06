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

import nl.knaw.dans.narcis.graphql.app.model.{ExternalWorkId, PersonId, Work, WorkId}
import nl.knaw.dans.narcis.graphql.app.repository.WorkDao
import nl.knaw.dans.narcis.graphql.app.repository.pigraph_impl.PidGraphWorkDao

// note that we only use the pidgraph, but could also try to get 'work' info from SRU!
class NarcisWorkDao(pidgraph: PidGraphWorkDao) extends WorkDao {
  override def getById(id: WorkId): Option[Work] = {
    pidgraph.getById(id)
  }

  override def getById(ids: Seq[WorkId]): Seq[Work] = {
    pidgraph.getById(ids)
  }

  override def getByPersonId(id: PersonId): Option[Seq[Work]] = {
    pidgraph.getByPersonId(id)
  }

  override def getByPersonId(ids: Seq[PersonId]): Seq[(PersonId, Seq[Work])] = {
    pidgraph.getByPersonId(ids)
  }

  override def getPersonsByWork(id: WorkId): Option[Seq[PersonId]] = {
    pidgraph.getPersonsByWork(id)
  }

  override def getPersonsByWork(ids: Seq[WorkId]): Seq[(WorkId, Seq[PersonId])] = ???

  override def getExtIds(id: WorkId): Seq[ExternalWorkId] =
    pidgraph.getExtIds(id)
}
