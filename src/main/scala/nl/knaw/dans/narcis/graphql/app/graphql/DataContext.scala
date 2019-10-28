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
package nl.knaw.dans.narcis.graphql.app.graphql

import java.sql.Connection

import nl.knaw.dans.narcis.graphql.app.graphql.types.{ Mutation, Query }
import nl.knaw.dans.narcis.graphql.app.repository.Repository

import scala.concurrent.ExecutionContext

class DataContext(connection: Connection,
                  repository: Connection => Repository,
                  val query: Query,
                  val mutation: Mutation,
                 )(implicit val executionContext: ExecutionContext) {

  lazy val repo: Repository = repository(connection)
}

object DataContext {
  def apply(connection: Connection, repository: Connection => Repository)
           (implicit executionContext: ExecutionContext): DataContext = {
    new DataContext(connection, repository, new Query, new Mutation)(executionContext)
  }
}
