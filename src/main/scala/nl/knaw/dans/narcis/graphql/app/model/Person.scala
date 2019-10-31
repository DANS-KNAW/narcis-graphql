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

import org.joda.time.LocalDate

import scala.collection.mutable.ArrayBuffer

case class Person(personId: PersonId, // for Narcis this is the PRS
                  name: String, // the surname (last name)
                  email: Option[String]=None,
                  url: Option[String]=None,
                  givenname: Option[String]=None, // first name
                  initials: Option[String]=None,
                  prefix: Option[String]=None,
                  titles: Option[String]=None,
                  // demo stuff below
                  birthday: LocalDate,
                  place: String,
                 )
