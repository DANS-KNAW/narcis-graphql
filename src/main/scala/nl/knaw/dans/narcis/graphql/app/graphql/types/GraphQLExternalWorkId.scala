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

import nl.knaw.dans.narcis.graphql.app.model.ExternalWorkId
import sangria.macros.derive.{GraphQLDescription, GraphQLField, GraphQLName}

@GraphQLName("ExternalWorkId")
@GraphQLDescription("The (persistent) identifier for a work")
class GraphQLExternalWorkId(externalWorkId: ExternalWorkId) {

  @GraphQLField
  @GraphQLName("type")
  @GraphQLDescription("The type of identifier, specific for a registration authority or organisation")
  val idType = externalWorkId.idType

  @GraphQLField
  @GraphQLName("value")
  @GraphQLDescription("The actual identifier (unique code)")
  val idValue = externalWorkId.idValue
}
