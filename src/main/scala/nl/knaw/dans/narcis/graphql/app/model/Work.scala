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
package nl.knaw.dans.narcis.graphql.app.model

import nl.knaw.dans.narcis.graphql.app.model.WorkType.WorkType
import org.joda.time.LocalDate

object WorkType extends Enumeration {
  type WorkType = Value

  // @formatter:off
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
  // @formatter:on  // @formatter:on
}

case class Work(id: WorkId,
                title: String,
                date: LocalDate,
                workType: WorkType.WorkType,
               )
