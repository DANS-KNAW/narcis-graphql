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

import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import nl.knaw.dans.narcis.graphql.Command.configuration
import nl.knaw.dans.narcis.graphql.app.model.{ExternalPersonId, Person, PersonId, PersonIdType}
import nl.knaw.dans.narcis.graphql.app.repository.PersonDao
import nl.knaw.dans.narcis.graphql.app.rest.{GraphPerson, GraphPersonPid, HttpWorker, PersonPidType, PidGraphData}
import org.apache.commons.lang.NotImplementedException
import org.json4s.Formats

import scala.util.{Failure, Success, Try}

class PidGraphpersonDao extends PersonDao with DebugEnhancedLogging {
  implicit val formats: Formats = PidGraphData.jsonFormats

  override def getAll: Seq[Person] = ???

  override def find(id: PersonId): Option[Person] = {
    // not much 'person' information available here, just that it is in there might be interesting to know
    ???
  }

  override def find(ids: Seq[PersonId]): Seq[Person] = ???

  override def getExtIds(id: PersonId): Seq[ExternalPersonId] = {
    trace(id)

    // this is what we can get additional information on here
    // get data from the pidgraph rest api
    val dataFetcher = new HttpWorker(configuration.version)
    val pidGraphPerson = dataFetcher.getJsonData[GraphPerson](s"${configuration.pidGraphUrl}/person/$id")
    // extract ext ids
    pidGraphPerson match {
      case Success(pgd) => {
        pgd.pids.map(pgPid => ExternalPersonIdFromPidGraphPersonId(pgPid)).flatMap(_.toOption)
      }
      case Failure(exception) => {
        logger.warn(s"Failed getting external identifiers for person $id, error: ${exception.getMessage}")
        Seq.empty[ExternalPersonId]
      }
    }
  }

  def ExternalPersonIdFromPidGraphPersonId(pgPid: GraphPersonPid): Try[ExternalPersonId] = Try {
    trace(pgPid)

    val idType = pgPid.pidType match {
      case PersonPidType.orcid => PersonIdType.orcid
      case PersonPidType.dai_nl => PersonIdType.dai_nl
      case PersonPidType.isni => PersonIdType.isni
      case PersonPidType.loop => PersonIdType.loop
      case PersonPidType.nod_person => PersonIdType.nod_person
      case PersonPidType.publication => PersonIdType.publication
      case PersonPidType.researcherid => PersonIdType.researcherid
      case PersonPidType.scopus => PersonIdType.scopus
      case PersonPidType.viaf => PersonIdType.viaf
      case _ =>
        logger.warn(s"No mapping implemented for Person Pid type ${pgPid.pidType}")
        throw new NotImplementedException(s"No mapping implemented for Person Pid type ${pgPid.pidType}")
    }

    new ExternalPersonId(idType, pgPid.value) // assume pidgraph value is normalized
  }
}
