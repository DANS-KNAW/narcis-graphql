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
package nl.knaw.dans.narcis.graphql.app.repository.narcis_impl

import java.sql.Connection

import nl.knaw.dans.narcis.graphql.app.database.VsoiDb
import nl.knaw.dans.narcis.graphql.app.repository.Repository
import nl.knaw.dans.narcis.graphql.app.repository.demo_impl.{ DemoPersonDao, DemoWorkDao }
import nl.knaw.dans.narcis.graphql.app.repository.vsoi_impl.VsoiPersonDao

class NarcisRepo(vsoiDb: VsoiDb)(implicit connection: Connection) {

  def repository: Repository = {
    val vsoiPersonDao = new VsoiPersonDao(vsoiDb)
    val demoPersonDao = new DemoPersonDao() // TODO replace with DAOs that get person data from other sources
    
    Repository(
      new NarcisPersonDao(vsoiPersonDao, demoPersonDao),
      new DemoWorkDao() // TODO replace with NarcisWorkDao, which combines the data from various sources
    )
  }
}
