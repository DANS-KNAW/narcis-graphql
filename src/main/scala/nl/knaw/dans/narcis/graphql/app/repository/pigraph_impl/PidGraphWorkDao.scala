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
package nl.knaw.dans.narcis.graphql.app.repository.pigraph_impl

import java.util.UUID

import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import nl.knaw.dans.narcis.graphql.Command.configuration
import nl.knaw.dans.narcis.graphql.app.model.{InputWork, PersonId, Work, WorkId}
import nl.knaw.dans.narcis.graphql.app.repository.WorkDao
import nl.knaw.dans.narcis.graphql.app.rest.{GraphWork, HttpWorker, PidGraphData}
import org.json4s.Formats

import scala.util.{Failure, Success, Try}

class PidGraphWorkDao extends WorkDao with DebugEnhancedLogging {
  implicit val formats: Formats = PidGraphData.jsonFormats

  override def getById(id: WorkId): Option[Work] = ???

  override def getById(ids: Seq[WorkId]): Seq[Work] = ???

  override def getByPersonId(id: PersonId): Option[Seq[Work]] = {
    trace(id)

    // get data from the pidgraph rest api
    val url = s"${configuration.pidGraphUrl}/person/$id/work"
    val dataFetcher = new HttpWorker(configuration.version)
    val pidGraphWorks = dataFetcher.getJsonData[Seq[GraphWork]](url)

    pidGraphWorks match {
      case Success(pgWorks) => {
        Some(pgWorks.map(pgWork => Work(pgWork.id, pgWork.title)))
      }
      case Failure(exception) => {
        logger.warn(s"Failed getting works for person $id from $url, error: ${exception.getMessage}")
        None
      }
    }

    // just return some fake work, for testing
    //Some(List(Work("fakeid", "Title-test-1")))
  }

  override def getByPersonId(ids: Seq[PersonId]): Seq[(PersonId, Seq[Work])] = {
    ids.map(id => (id, getByPersonId(id))).filter {
      case (id, worksOpt) => worksOpt.isDefined
    }.map{
      case (id, worksOpt) => (id,worksOpt.get)
    }
  }

  override def store(personIds: Seq[PersonId], work: InputWork): Work = ???

  override def getPersonsByWork(id: WorkId): Option[Seq[PersonId]] = ???

  override def getPersonsByWork(ids: Seq[WorkId]): Seq[(WorkId, Seq[PersonId])] = ???
}
