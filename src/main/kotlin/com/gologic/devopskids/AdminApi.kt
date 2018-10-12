package com.gologic.devopskids;

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/*********************************************
 * Rest Controller with all CRUD Operations
 *********************************************/

/**
 *  KID
 */
@RestController
class KidRestController(private val repository: KidRepository, private val imageFileRepository: ImageFileRepository) {

  private val log = LoggerFactory.getLogger(KidRestController::class.java)

  @GetMapping("/api/admin/kids")
  fun list() = repository.findAllByOrderByUsername()

  @GetMapping("/api/admin/kids/{username}")
  fun get(@PathVariable username: String) = repository.findById(username).orElseThrow { NoSuchElementException() }

  @PostMapping("/api/admin/kids")
  fun save(@RequestBody kid: Kid):Kid{
    // in case of UPDATE, DO NOT OVERRIDE AVATAR FILE (KidRestController::addAvatar is designed for it)
    val entity = repository.findById(kid.username)
    if(entity.isPresent) {
      kid.avatar = entity.get().avatar
    }
    return repository.save(kid)
  }

  @DeleteMapping("/api/admin/kids/{username}")
  fun delete(@PathVariable username: String) = repository.deleteById(username)

  @PostMapping("/api/admin/kids/{username}/avatar")
  fun addAvatar(@PathVariable username: String, @RequestParam("file") file: MultipartFile) {
    var kid = get(username)
    // Save avatar image file
    kid.avatar = imageFileRepository.save(ImageFile(null, file.originalFilename.toString(), file.contentType, file.bytes))
    repository.save(kid)
  }

}

/**
 *  GALLERY
 */
@RestController
class GalleryRestController(private val repository: GalleryRepository, private val imageFileRepository: ImageFileRepository) {

  private val log = LoggerFactory.getLogger(KidRestController::class.java)

  @GetMapping("/api/admin/galleries")
  fun list() = repository.findAll()

  @GetMapping("/api/admin/galleries/{id}")
  fun get(@PathVariable id: Long) = repository.findById(id).orElseThrow { NoSuchElementException() }

  @PostMapping("/api/admin/galleries")
  fun save(@RequestBody gallery: Gallery): Gallery {
    // in case of UPDATE, DO NOT OVERRIDE PREVIEW FILE (GalleryRestController::addPreview is designed for it)
    gallery.id?.let {
      gallery.preview = get(gallery.id).preview
    }    
    return repository.save(gallery)
  }

  @DeleteMapping("/api/admin/galleries/{id}")
  fun delete(@PathVariable id: Long) = repository.deleteById(id)

  @PostMapping("/api/admin/galleries/{id}/preview")
  fun addPreview(@PathVariable id: Long, @RequestParam("file") file: MultipartFile) {
    var gallery = get(id)
    // Save preview image file
    gallery.preview = imageFileRepository.save(ImageFile(null, file.originalFilename.toString(), file.contentType, file.bytes))
    repository.save(gallery)
  }

}

/**
 *  DRAWING
 */
@RestController
class DrawingRestController(private val galleryRepository: GalleryRepository, private val kidRepository: KidRepository, private val drawingRepository: DrawingRepository, private val imageFileRepository: ImageFileRepository) {

  private val log = LoggerFactory.getLogger(DrawingRestController::class.java)

  @GetMapping("/api/admin/galleries/{id}/drawings")
  fun galleryDrawingList(@PathVariable id: Long) = drawingRepository.findAllByGalleryId(id)

  @GetMapping("/api/admin/kids/{username}/drawings")
  fun kidDrawingList(@PathVariable username: String) = drawingRepository.findAllByAuthorUsername(username)

  @PostMapping("/api/admin/galleries/{id}/drawings/{username}")
  fun addDrawing(@PathVariable id: Long, @PathVariable username: String, @RequestParam("file") file: MultipartFile, @RequestParam("title") title: String) {
    // Save image file
    val image = imageFileRepository.save(ImageFile(null, file.originalFilename.toString(), file.contentType, file.bytes))
    // Save drawing
    drawingRepository.save(Drawing(null, title, image, kidRepository.findById(username).get(), galleryRepository.findById(id).get()))
  }

  @DeleteMapping("/api/admin/galleries/{id}/drawings/{drawingId}")
  fun delete(@PathVariable id: Long, @PathVariable drawingId: Long) {
    val drawing = drawingRepository.findByGalleryIdAndId(id, drawingId)
    drawingRepository.delete(drawing);
  }
}