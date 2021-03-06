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
import nl.knaw.dans.narcis.graphql.app.graphql.relay.ExtendedConnection
import nl.knaw.dans.narcis.graphql.app.graphql.resolvers.{ PersonResolver, WorkResolver }
import nl.knaw.dans.narcis.graphql.app.model.{ PersonId, WorkId }
import sangria.macros.derive.{ GraphQLDescription, GraphQLField }
import sangria.relay.ConnectionArgs
import sangria.schema.{ Context, DeferredValue }

class Query {

  @GraphQLField
  @GraphQLDescription("List all known persons.")
  def persons(before: Option[String] = None,
              after: Option[String] = None,
              first: Option[Int] = None,
              last: Option[Int] = None,
             )(implicit ctx: Context[DataContext, Unit]): ExtendedConnection[GraphQLPerson] = {
    val results = ctx.ctx.repo.personDao.getAll

    ExtendedConnection.connectionFromSeq(
      results.map(new GraphQLPerson(_)),
      ConnectionArgs(before, after, first, last),
    )
  }

  // NOTE: toggle between these 2 implementations and see the difference
  //  in the number of interactions with the DAO
//  @GraphQLField
//  @GraphQLDescription("Find the person identified with the given identifier.")
//  def person(@GraphQLDescription("The identifier of the person to be found.") id: PersonId)
//            (implicit ctx: Context[DataContext, Unit]): Option[GraphQLPerson] = {
//    ctx.ctx.repo.personDao.find(id)
//      .map(new GraphQLPerson(_))
//  }

  @GraphQLField
  @GraphQLDescription("Find the person identified with the given (internal) identifier.")
  def person(@GraphQLDescription("The identifier of the person to be found.") id: PersonId)
            (implicit ctx: Context[DataContext, Unit]): DeferredValue[DataContext, Option[GraphQLPerson]] = {
    PersonResolver.personById(id)
      .map(_.map(new GraphQLPerson(_)))
  }

  @GraphQLField
  @GraphQLDescription("Find the work identified with the given (internal) identifier.")
  def work(@GraphQLDescription("The identifier of the work to be found.") id: WorkId)
            (implicit ctx: Context[DataContext, Unit]): DeferredValue[DataContext, Option[GraphQLWork]] = {
    WorkResolver.workById(id)
      .map(_.map(new GraphQLWork(_)))
  }
}
