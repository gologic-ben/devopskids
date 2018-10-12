package com.gologic.devopskids

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.time.Month

/**
 * A single repository test to validate JPA entities relationship
 */
@RunWith(SpringRunner::class)
@DataJpaTest
class RepositoriesTests {
    
  lateinit @Autowired var entityManager: TestEntityManager
  lateinit @Autowired var galleryRepository: GalleryRepository

  @Test
  fun `When adding a draw then return gallery from date`() {

    // Add kids
    val xavier = Kid("xavier");
    entityManager.persist(xavier)

    // Add a gallery
    val september = LocalDate.of(2018, Month.SEPTEMBER, 1)
    val gallery = Gallery(null, "Continuous delivery", september, "Our first gallery !", ImageFile())
    entityManager.persist(gallery)
    
    // Add a drawing
    val drawing = Drawing(null, "La livraison continue", ImageFile(), xavier, gallery)
    entityManager.persist(drawing)
    entityManager.flush()

    // Search for gallery of september and check author list
    val found = galleryRepository.findByDate(september)
    assert(found.equals(gallery))

    // Search for gallery of september and check author list
    //val october = LocalDate.of(2018, Month.OCTOBER, 1)   
    //val notfound = galleryRepository.findOneByDate(october)
    //assertNull(notfound)

  }


}