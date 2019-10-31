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

import java.util.UUID

import nl.knaw.dans.narcis.graphql.app.graphql.resolvers.{PersonResolver, WorkResolver}
import nl.knaw.dans.narcis.graphql.app.graphql.types.{GraphQLExternalPersonId, GraphQLPerson, GraphQLWork, Query}
import nl.knaw.dans.narcis.graphql.app.model.PersonIdType.PersonIdType
import nl.knaw.dans.narcis.graphql.app.model.{PersonIdType}
import org.joda.time.LocalDate
import sangria.ast.StringValue
import sangria.execution.deferred.DeferredResolver
import sangria.macros.derive.{DocumentInputField, _}
import sangria.marshalling.FromInput
import sangria.schema.{InputObjectType, ObjectType, ScalarType, Schema}
import sangria.validation.{StringCoercionViolation, ValueCoercionViolation, Violation}

import scala.util.Try

object GraphQLSchema {

  case object UUIDCoercionViolation extends ValueCoercionViolation("UUID value expected")
  case object DateCoercionViolation extends ValueCoercionViolation("Date value expected")

  implicit val UUIDType: ScalarType[UUID] = {
    def parseUUID(s: String): Either[Violation, UUID] = {
      Try { UUID.fromString(s) }
        .fold(_ => Left(UUIDCoercionViolation), Right(_))
    }

    ScalarType("UUID",
      description = Some("The UUID scalar type represents textual data, " +
        "formatted as a universally unique identifier."),
      coerceOutput = (value, _) => value.toString,
      coerceUserInput = {
        case s: String => parseUUID(s)
        case _ => Left(StringCoercionViolation)
      },
      coerceInput = {
        case StringValue(s, _, _, _, _) => parseUUID(s)
        case _ => Left(StringCoercionViolation)
      }
    )
  }

  implicit val LocalDateType: ScalarType[LocalDate] = {
    def parseDate(s: String): Either[Violation, LocalDate] = {
      Try { LocalDate.parse(s) }
        .fold(_ => Left(DateCoercionViolation), Right(_))
    }

    ScalarType("LocalDate",
      description = Some("A LocalDate scalar type represents textual data."),
      coerceOutput = (value, _) => value.toString("yyyy-MM-dd"),
      coerceUserInput = {
        case s: String => parseDate(s)
        case _ => Left(DateCoercionViolation)
      },
      coerceInput = {
        case StringValue(s, _, _, _, _) => parseDate(s)
        case _ => Left(DateCoercionViolation)
      }
    )
  }

  implicit val PersonIdTypeType = deriveEnumType[PersonIdType.Value](
    EnumTypeDescription("The type of person (author) identifier"),
    DocumentValue("dai_nl", "Digital Author Identifier (DAI), but specific for The Netherlands"),
    DocumentValue("orcid", "Open Researcher and Contributor ID (ORCID)"),
    // TODO all the others...
  )

  implicit val GraphQLExternalPersonIdType: ObjectType[DataContext, GraphQLExternalPersonId] = deriveObjectType[DataContext, GraphQLExternalPersonId]()

  implicit val GraphQLPersonType: ObjectType[DataContext, GraphQLPerson] = deriveObjectType[DataContext, GraphQLPerson]()

  implicit val GraphQLWorkType: ObjectType[DataContext, GraphQLWork] = deriveObjectType[DataContext, GraphQLWork]()

  implicit val QueryType: ObjectType[DataContext, Unit] = deriveContextObjectType[DataContext, Query, Unit](_.query)

  val schema: Schema[DataContext, Unit] = Schema[DataContext, Unit](QueryType)
  val deferredResolver: DeferredResolver[DataContext] = DeferredResolver.fetchers(
    PersonResolver.byIdFetcher,
    WorkResolver.byIdFetcher,
    WorkResolver.byPersonIdFetcher,
    WorkResolver.authorWorkIdFetcher,
  )
}
