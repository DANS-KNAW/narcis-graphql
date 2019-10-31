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
package nl.knaw.dans.narcis.graphql.app.graphql.types

import nl.knaw.dans.narcis.graphql.app.graphql.DataContext
import nl.knaw.dans.narcis.graphql.app.graphql.resolvers.WorkResolver
import nl.knaw.dans.narcis.graphql.app.model.PersonIdType.PersonIdType
import nl.knaw.dans.narcis.graphql.app.model.{Person, PersonId, PersonIdType}
import org.joda.time.LocalDate
import sangria.macros.derive.{GraphQLDescription, GraphQLField, GraphQLName}
import sangria.schema.{Context, DeferredValue}

@GraphQLName("Person")
@GraphQLDescription("The object containing data about the person.")
class GraphQLPerson(private val person: Person) {

  @GraphQLField
  @GraphQLName("id")
  @GraphQLDescription("The identifier with which this person is associated.")
  val personId: PersonId = person.personId

  @GraphQLField
  @GraphQLDescription("The person's name.")
  val name: String = person.name

  @GraphQLField
  @GraphQLDescription("The person's email.")
  val email: Option[String] = person.email

  @GraphQLField
  @GraphQLDescription("The person's URL.")
  val url: Option[String] = person.url

  @GraphQLField
  @GraphQLDescription("The person's givenname.")
  val givenname: Option[String] = person.givenname

  @GraphQLField
  @GraphQLDescription("The person's initials.")
  val initials: Option[String] = person.initials

  @GraphQLField
  @GraphQLDescription("The person's name prefix.")
  val prefix: Option[String] = person.prefix

  @GraphQLField
  @GraphQLDescription("The person's titles.")
  val titles: Option[String] = person.titles

  // demo fields below

  @GraphQLField
  @GraphQLDescription("The date the person was born.")
  val birthday: LocalDate = person.birthday

  @GraphQLField
  @GraphQLDescription("The city/town where this person lives.")
  val place: String = person.place

  @GraphQLField
  @GraphQLDescription("The external identifiers of this person")
  def externalIds(implicit ctx: Context[DataContext, GraphQLPerson]): Seq[GraphQLExternalPersonId] = {
    // without a resolver
    ctx.ctx.repo.personDao.getExtIds(person.personId).map(new GraphQLExternalPersonId(_))
  }

  @GraphQLField
  @GraphQLDescription("External identifiers of a specified type")
  def externalIdsByType(@GraphQLName("type") idType: PersonIdType)
                       (implicit ctx: Context[DataContext, GraphQLPerson]): Seq[GraphQLExternalPersonId] = {
    // also without a resolver
    // using a filter here, might be suboptimal, maybe do it in the repo or db?
    ctx.ctx.repo.personDao.getExtIds(person.personId)
      .filter(eId => eId.idType == idType)
      .map(new GraphQLExternalPersonId(_))
  }

  // NOTE: toggle between these 2 implementations and see the difference
  //  in the number of interactions with the DAO
//  @GraphQLField
//  @GraphQLDescription("List all works of this person.")
//  def works(implicit ctx: Context[DataContext, GraphQLPerson]): Option[Seq[GraphQLWork]] = {
//    ctx.ctx.repo.workDao.getByPersonId(personId)
//      .map(_.map(new GraphQLWork(_)))
//  }

  @GraphQLField
  @GraphQLDescription("List all works of this person.")
  def works(implicit ctx: Context[DataContext, GraphQLPerson]): DeferredValue[DataContext, Option[Seq[GraphQLWork]]] = {
    WorkResolver.worksByPersonId(personId)
      .map(_.map(_.map(new GraphQLWork(_))))
  }
}
