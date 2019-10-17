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
package nl.knaw.dans.narcis.graphql.app.database

import java.sql.{Connection, ResultSet}

import nl.knaw.dans.lib.error._
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import nl.knaw.dans.narcis.graphql.app.model.Person
import org.joda.time.LocalDate
import resource.{ManagedResource, managed}

import scala.util.Try

class VsoiDb() extends DebugEnhancedLogging {

  def getPerson(prsId: String)(implicit connection: Connection): Try[Option[Person]] = {
    trace(prsId)

    val query = "SELECT achternaam FROM persoon WHERE pers_id=?;"
    val managedResultSet: ManagedResource[ResultSet] = for {
          prepStatement <- managed(connection.prepareStatement(query))
          _ = prepStatement.setString(1, prsId)
          resultSet <- managed(prepStatement.executeQuery())
        } yield resultSet

    // Note that we only get the achternaam, the rest is FAKE!
    managedResultSet.map(resultSet => {
      if (resultSet.next())
        Option(Person(prsId, resultSet.getString("achternaam"), new LocalDate(1990, 1, 1), "London"))
      else
        Option.empty
    }).tried
  }


//  def getExternalIdentifiers(prsId: String)
//                            (implicit connection: Connection): Try[Seq[String]] = {
//    trace(prsId)
//
//    val query = "Select extern_id from persoon_externid where pers_id=?;"
//    val resultSet = for {
//      prepStatement <- managed(connection.prepareStatement(query))
//      _ = prepStatement.setString(1, prsId)
//      resultSet <- managed(prepStatement.executeQuery())
//    } yield resultSet
//
//    resultSet.map(resultSet => {
//      /*
//       * Stream.continually(...).takeWhile(b => b) is equivalent to Java's while-loop: it checks
//       * whether there is a next result and continues doing this until resultSet.next() returns false.
//       */
//      Stream.continually(resultSet.next())
//        .takeWhile(b => b)
//        .flatMap(_ =>
//          resultSet.getString("extern_id")
//        )
//        .toList // We can't leave it as a Stream, but need to convert to a List, since the resources are closed on calling .tried
//    }
//    ).tried
//  }
//

}
