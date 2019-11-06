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

import java.text.SimpleDateFormat

import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import nl.knaw.dans.narcis.graphql.Command.configuration
import nl.knaw.dans.narcis.graphql.app.model.WorkType.WorkType
import nl.knaw.dans.narcis.graphql.app.rest.{GraphPerson, GraphWork, GraphWorkPid, HttpWorker, PidGraphData, WorkPidType, WorkType => GraphWorkType}
import nl.knaw.dans.narcis.graphql.app.model.{ExternalWorkId, PersonId, Work, WorkId, WorkIdType, WorkType}
import nl.knaw.dans.narcis.graphql.app.repository.WorkDao
import org.apache.commons.lang.NotImplementedException
import org.json4s.Formats
import org.joda.time.LocalDate

import scala.util.{Failure, Success, Try}

class PidGraphWorkDao extends WorkDao with DebugEnhancedLogging {
  implicit val formats: Formats = PidGraphData.jsonFormats

  override def getById(id: WorkId): Option[Work] = {
    trace(id)

    // date from pidgraph is formatted yyyyMMdd
    val pgDateFormat = new SimpleDateFormat("yyyyMMdd")

    // get data from the pidgraph rest api
    val url = s"${configuration.pidGraphUrl}/work/$id"
    val dataFetcher = new HttpWorker(configuration.version)
    val pidGraphWork = dataFetcher.getJsonData[GraphWork](url)

    pidGraphWork match {
      case Success(pgWork) => {

        Some( Work(pgWork.id,
          pgWork.title,
          new LocalDate(pgDateFormat.parse(pgWork.date)),
          WorkTypeFromPidGraphWorkType(pgWork.entitytype))
        )
      }
      case Failure(exception) => {
        logger.warn(s"Failed getting works for person $id from $url, error: ${exception.getMessage}")
        None
      }
    }
  }

  override def getById(ids: Seq[WorkId]): Seq[Work] = {
    ids.map(id => (id, getById(id)))
      .collect { case (id, Some(work)) =>  work } // no id !
  }

  override def getByPersonId(id: PersonId): Option[Seq[Work]] = {
    trace(id)

    // date from pidgraph is formatted yyyyMMdd
    val pgDateFormat = new SimpleDateFormat("yyyyMMdd")

    // get data from the pidgraph rest api
    val url = s"${configuration.pidGraphUrl}/person/$id/work"
    val dataFetcher = new HttpWorker(configuration.version)
    val pidGraphWorks = dataFetcher.getJsonData[Seq[GraphWork]](url)

    pidGraphWorks match {
      case Success(pgWorks) => {

        Some(pgWorks.map(pgWork => Work(pgWork.id,
                                        pgWork.title,
                                        new LocalDate(pgDateFormat.parse(pgWork.date)),
                                        WorkTypeFromPidGraphWorkType(pgWork.entitytype))
          )
        )
      }
      case Failure(exception) => {
        logger.warn(s"Failed getting works for person $id from $url, error: ${exception.getMessage}")
        None
      }
    }

  }

  override def getByPersonId(ids: Seq[PersonId]): Seq[(PersonId, Seq[Work])] = {
    ids.map(id => (id, getByPersonId(id)))
      .collect { case (id, Some(works)) => (id, works) }
  }

  override def getPersonsByWork(id: WorkId): Option[Seq[PersonId]] = {
    trace(id)

    // get data from the pidgraph rest api
    val dataFetcher = new HttpWorker(configuration.version)
    val pidGraphPersons = dataFetcher.getJsonData[Seq[GraphPerson]](s"${configuration.pidGraphUrl}/work/$id/person")
    // extract ext ids
    pidGraphPersons match {
      case Success(pgd) => {
        // Note that we throw away all person data and only get the id
        Some(pgd.map(pgPerson => pgPerson.id))
      }
      case Failure(exception) => {
        logger.warn(s"Failed getting external identifiers for work $id, error: ${exception.getMessage}")
        None
      }
    }
  }

  override def getPersonsByWork(ids: Seq[WorkId]): Seq[(WorkId, Seq[PersonId])] = ???

  def WorkTypeFromPidGraphWorkType(pgWorkType: GraphWorkType.WorkType): WorkType = {
    pgWorkType match {
      case  GraphWorkType.publication => WorkType.publication
      case  GraphWorkType.journalArticle => WorkType.journalArticle
      case  GraphWorkType.dataset => WorkType.dataset
      case  GraphWorkType.software => WorkType.software
      case  GraphWorkType.conferencePaper => WorkType.conferencePaper
      case  GraphWorkType.book => WorkType.book
      case  GraphWorkType.bookChapter => WorkType.bookChapter
      case  GraphWorkType.dataSet => WorkType.dataSet
      case  GraphWorkType.report => WorkType.report
      case  GraphWorkType.dissertation => WorkType.dissertation
      case  GraphWorkType.workingPaper => WorkType.workingPaper
      case  GraphWorkType.bookReview => WorkType.bookReview
      case  GraphWorkType.bookPart => WorkType.bookPart
      case  GraphWorkType.annotation => WorkType.annotation
      case  GraphWorkType.lecture => WorkType.lecture
      case  GraphWorkType.conferenceObject => WorkType.conferenceObject
      case  GraphWorkType.conferenceProceedings => WorkType.conferenceProceedings
      case  GraphWorkType.patent => WorkType.patent
      case  GraphWorkType.preprint => WorkType.preprint
      case  GraphWorkType.review => WorkType.review
      case  GraphWorkType.conferenceItem => WorkType.conferenceItem
      case  GraphWorkType.masterThesis => WorkType.masterThesis
      case  GraphWorkType.studentThesis => WorkType.studentThesis
      case  GraphWorkType.bachelorThesis => WorkType.bachelorThesis
      case  GraphWorkType.technicaldocumentation => WorkType.technicaldocumentation
      case  GraphWorkType.other => WorkType.other
      case  _ => WorkType.other
    }
  }

  def ExternalWorkIdFromPidGraphWorkId(pgWorkId: GraphWorkPid) : Try[ExternalWorkId] = Try {
    val workIdType = pgWorkId.pidType match {
      case  WorkPidType.arxiv => WorkIdType.arxiv
      case  WorkPidType.doi  => WorkIdType.doi
      case  WorkPidType.eid => WorkIdType.eid
      case  WorkPidType.handle => WorkIdType.handle
      case  WorkPidType.narcis_oaipub => WorkIdType.narcis_oaipub
      case  WorkPidType.pmc => WorkIdType.pmc
      case  WorkPidType.pmid => WorkIdType.pmid
      case  WorkPidType.purl => WorkIdType.purl
      case  WorkPidType.urn_nbn => WorkIdType.urn_nbn
      case  WorkPidType.wosuid => WorkIdType.wosuid
      case  _ => {
        logger.warn(s"No mapping implemented for Work Id type ${pgWorkId.pidType}")
        throw new NotImplementedException(s"No mapping implemented for Work Id type ${pgWorkId.pidType}")
      }
    }

    new ExternalWorkId(workIdType, pgWorkId.value) // assume pidgraph value is normalized
  }

  override def getExtIds(id: WorkId): Seq[ExternalWorkId] = {
    trace(id)

    // get data from the pidgraph rest api
    val dataFetcher = new HttpWorker(configuration.version)
    val pidGraphWork = dataFetcher.getJsonData[GraphWork](s"${configuration.pidGraphUrl}/work/$id")
    // extract ext ids
    pidGraphWork match {
      case Success(pgd) => {
        pgd.pids.map(pgPid => ExternalWorkIdFromPidGraphWorkId(pgPid)).flatMap(_.toOption)
      }
      case Failure(exception) => {
        logger.warn(s"Failed getting external identifiers for work $id, error: ${exception.getMessage}")
        Seq.empty[ExternalWorkId]
      }
    }
  }
}
