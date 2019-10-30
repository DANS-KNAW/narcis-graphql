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
package nl.knaw.dans.narcis.graphql.app.rest

import nl.knaw.dans.narcis.graphql.app.rest.KnownGraphTypes.KnownGraphType
import nl.knaw.dans.narcis.graphql.app.rest.PersonPidType.PersonPidType
import nl.knaw.dans.narcis.graphql.app.rest.WorkPidType.WorkPidType
import nl.knaw.dans.narcis.graphql.app.rest.WorkType.WorkType
import org.json4s.ext.EnumNameSerializer
import org.json4s.{DefaultFormats, Formats}

object PersonPidType extends Enumeration {
  type PersonPidType = Value

  // @formatter:off
  val publication   = Value("publication")
  val dai_nl        = Value("dai-nl")
  val isni          = Value("isni")
  val loop          = Value("loop")
  val nod_person    = Value("nod-person")
  val orcid         = Value("orcid")
  val researcherid  = Value("researcherid")
  val scopus        = Value("scopus")
  val viaf          = Value("viaf")
  // @formatter:on
}

object WorkPidType extends Enumeration {
  type WorkPidType = Value

  // @formatter:off
  val arxiv          = Value("arxiv")
  val doi            = Value("doi")
  val eid            = Value("eid")
  val handle         = Value("handle")
  val narcis_oaipub  = Value("narcis-oaipub")
  val pmc            = Value("pmc")
  val pmid           = Value("pmid")
  val purl           = Value("purl")
  val urn_nbn        = Value("urn:nbn")
  val wosuid         = Value("wosuid")
  // @formatter:on
}

object WorkType extends Enumeration {
  type WorkType = Value

  // @formatter:off
  val publication            = Value("publication")
  val journalArticle         = Value("journal-article")
  val dataset                = Value("dataset")
  val software               = Value("software")
  val conferencePaper        = Value("conference-paper")
  val book                   = Value("book")
  val bookChapter            = Value("book-chapter")
  val dataSet                = Value("data-set")
  val report                 = Value("report")
  val dissertation           = Value("dissertation")
  val workingPaper           = Value("working-paper")
  val bookReview             = Value("book-review")
  val bookPart               = Value("book-part")
  val annotation             = Value("annotation")
  val lecture                = Value("lecture")
  val conferenceObject       = Value("conference-object")
  val conferenceProceedings  = Value("conference-proceedings")
  val patent                 = Value("patent")
  val preprint               = Value("preprint")
  val review                 = Value("review")
  val conferenceItem         = Value("conference-item")
  val masterThesis           = Value("master-thesis")
  val studentThesis          = Value("student-thesis")
  val bachelorThesis         = Value("bachelor-thesis")
  val technicaldocumentation = Value("technicaldocumentation")
  val other                  = Value("other")
  // @formatter:on
}

// The following 'type' information is from graph database tables, and can be extended
// Maybe these can be retrieved from the graph database
object KnownGraphTypes extends Enumeration {
  type KnownGraphType = Value

  val NarcisIdx: KnownGraphType = Value("narcis-idx")
  val Orcid: KnownGraphType = Value("orcid")
  val SysVsoi: KnownGraphType = Value("sysvsoi")
}

case class GraphPersonPid(
                           source: KnownGraphType,
                           pidType: PersonPidType,
                           value: String,
                         )

case class GraphWorkPid(
                         source: KnownGraphType,
                         pidType: WorkPidType,
                         value: String,
                       )

case class GraphPerson(
                        id: String, // the PRS ID
                        pids: Seq[GraphPersonPid],
                      )

case class GraphWork(
                      title: String, // Work.title
                      entitytype: WorkType,
                      date: String, // publication date maybe DateTime?
                      pids: Seq[GraphWorkPid],
                    )

case class PidGraphData(person: GraphPerson, works: Seq[GraphWork])
object PidGraphData {
  implicit val jsonFormats: Formats = new DefaultFormats {} ++ List(WorkType, WorkPidType, PersonPidType, KnownGraphTypes).map(new EnumNameSerializer(_))
}
