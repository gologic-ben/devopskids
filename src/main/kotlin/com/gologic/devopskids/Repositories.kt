package com.gologic.devopskids

import org.springframework.data.repository.CrudRepository
import java.time.LocalDate

interface GalleryRepository : CrudRepository<Gallery, Long> {
  fun findByDate(date: LocalDate): Gallery
}

interface KidRepository : CrudRepository<Kid, String> {
  fun findAllByOrderByUsername(): Iterable<Kid>
}

interface DrawingRepository : CrudRepository<Drawing, String> {
  fun findAllByGalleryId(id: Long?): Iterable<Drawing>
  fun findAllByAuthorUsername(username: String): Iterable<Drawing>
  fun findByGalleryIdAndId(galleryId: Long, id: Long): Drawing
}
interface ImageFileRepository : CrudRepository<ImageFile, String>