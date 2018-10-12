package com.gologic.devopskids;

import org.slf4j.LoggerFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.File
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletResponse

// DONT FORGET TO IMPORT org.springframework.ui.set

@Controller
class HtmlController(private val kidRepository: KidRepository, private val drawingRepository: DrawingRepository, private val galleryRepository: GalleryRepository) {

  private val log = LoggerFactory.getLogger(HtmlController::class.java)

  @GetMapping("/")
  fun index(model: Model): String {
    log.info("HtmlController::index")
    
    model["title"] = "DevOps Kidz"
    return "index"
  }
  
  @GetMapping("/galleries")
  fun galleries(model: Model): String {
    log.info("HtmlController::galleries")

    SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    
    model["title"] = "DevOps Kids !"
    model["galleries"] = galleryRepository.findAll().map { it.render() }
    return "galleries"
  }

  @GetMapping("/galleries/{id}")
  fun gallery(@PathVariable id: Long, model: Model): String {
    log.info("HtmlController::galleries::{0}", id)

    val gallery = galleryRepository
            .findById(id)
            .orElseThrow { IllegalArgumentException("Wrong gallery id provided") }
            .render()

    // Search for drawings
    val drawings = drawingRepository.findAllByGalleryId(gallery.id)
    log.info("HtmlController::gallery::drawings::{0}", drawings)

    model["title"] = gallery.title
    model["gallery"] = gallery
    model["drawings"] = drawings
    return "gallery"
  }

  /**
   * Drawing files
   */
  @GetMapping("/gallery/{id}/{drawingId}")
  @ResponseBody
  fun downloadFile(@PathVariable id: Long, @PathVariable drawingId: Long, response: HttpServletResponse): FileSystemResource {
    val drawing = drawingRepository.findByGalleryIdAndId(id, drawingId)

    val tmpfile = File.createTempFile("devopskids", null)
    tmpfile.writeBytes(drawing.image.pic)
    
    response.setContentType(drawing.image.type);
    return FileSystemResource(tmpfile) 
  }

  /*
   * Allow rendering data class with a Rendered object (like a DTO)
   */
  fun Gallery.render() = RenderedGallery(title,description,id,date.format(DateTimeFormatter.ISO_LOCAL_DATE))
  data class RenderedGallery(val title: String,val description: String,val id: Long?,val date: String)

}