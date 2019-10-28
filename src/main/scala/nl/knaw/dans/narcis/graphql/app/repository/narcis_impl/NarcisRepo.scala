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
