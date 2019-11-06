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

import nl.knaw.dans.narcis.graphql.app.model.WorkIdType.WorkIdType

object WorkIdType extends Enumeration {
  type WorkIdType = Value

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

case class ExternalWorkId(idType: WorkIdType, idValue: String)
