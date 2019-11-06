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
import nl.knaw.dans.narcis.graphql.app.graphql.types.{GraphQLExternalPersonId, GraphQLExternalWorkId, GraphQLPerson, GraphQLWork, Query}
import nl.knaw.dans.narcis.graphql.app.model.{PersonIdType, WorkIdType, WorkType}
import org.joda.time.LocalDate
import sangria.ast.StringValue
import sangria.execution.deferred.DeferredResolver
import sangria.macros.derive._
import sangria.schema.{ObjectType, ScalarType, Schema}
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
    DocumentValue("nod_person", "NARCIS internal Identifier"),
    DocumentValue("dai_nl", "Digital Author Identifier (DAI), specific for The Netherlands"),
    DocumentValue("orcid", "Open Researcher and Contributor ID (ORCID)"),
    DocumentValue("isni", "International Standard Name Identifier (ISNI)"),
    DocumentValue("researcherid", "ResearcherID"),
    DocumentValue("scopus", "Scopus Author ID"),
    DocumentValue("viaf", "Virtual International Authority File (VIAF)"),
    DocumentValue("ror", "Research Organization Registry (ROR)"),
    //DocumentValue("loop", ""),
    //DocumentValue("publication", ""),
  )

  implicit val WorkTypeType = deriveEnumType[WorkType.Value](
    EnumTypeDescription("The type of work"),
    // no value description here, names are self explanatory
  )

  implicit val WorkIdTypeType = deriveEnumType[WorkIdType.Value](
    EnumTypeDescription("The type of work identifier"),
    DocumentValue("arxiv", "arXiv"),
    DocumentValue("doi", "Digital Object Identifier (DOI)"),
    DocumentValue("eid", "Scopus EID"),
    DocumentValue("handle", "Handle"),
    DocumentValue("pmc", "PubMed Central ID"),
    DocumentValue("pmid", "PubMed ID"),
    DocumentValue("purl", "Persistent uniform resource locator (PURL)"),
    DocumentValue("urn_nbn", "Uniform Resource Names - National Bibliographic Number (URN-NBN)"),
    DocumentValue("wosuid", "Web of Science UID"),
    //DocumentValue("narcis_oaipub", ""),
  )

  implicit val GraphQLExternalWorkIdType: ObjectType[DataContext, GraphQLExternalWorkId] = deriveObjectType[DataContext, GraphQLExternalWorkId]()

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
