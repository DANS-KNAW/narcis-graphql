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

import nl.knaw.dans.narcis.graphql.app.model.PersonIdType.PersonIdType

object PersonIdType extends Enumeration {
  type PersonIdType = Value

  // @formatter:off
  val publication   = Value("publication")
  val dai_nl        = Value("dai-nl")
  val isni          = Value("isni")
  val loop          = Value("loop")
  val nod_person    = Value("nod-person") // this is actually internal!
  val orcid         = Value("orcid")
  val researcherid  = Value("researcherid")
  val scopus        = Value("scopus")
  val viaf          = Value("viaf")
  val ror           = Value("ror")
  // @formatter:on
}

case class ExternalPersonId(idType: PersonIdType, idValue: String)
