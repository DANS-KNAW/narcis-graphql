package nl.knaw.dans.narcis.graphql.app.repository.narcis_impl

import nl.knaw.dans.narcis.graphql.app.model.{ InputPerson, Person, PersonId }
import nl.knaw.dans.narcis.graphql.app.repository.PersonDao
import nl.knaw.dans.narcis.graphql.app.repository.demo_impl.DemoPersonDao
import nl.knaw.dans.narcis.graphql.app.repository.vsoi_impl.VsoiPersonDao

class NarcisPersonDao(vsoi: VsoiPersonDao,
                      demo: DemoPersonDao,
                     ) extends PersonDao {

  override def getAll: Seq[Person] = ???

  override def find(id: PersonId): Option[Person] = ???

  override def find(ids: Seq[PersonId]): Seq[Person] = ???

  override def store(person: InputPerson): Person = ???
}
