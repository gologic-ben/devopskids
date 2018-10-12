package com.gologic.devopskids

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Entity
data class Kid(@Id val username: String, @OneToOne var avatar: ImageFile? = null)

@Entity
data class Gallery(
        @Id @GeneratedValue val id: Long? = null,
        val title: String,
        val date: LocalDate = LocalDate.now(),
        val description: String,
        @OneToOne var preview: ImageFile? = null)

@Entity
data class Drawing(
        @Id @GeneratedValue val id: Long? = null,
        val title: String,
        @OneToOne val image: ImageFile,
        @ManyToOne @JoinColumn val author: Kid,
        @ManyToOne @JoinColumn val gallery: Gallery)

@Entity
data class ImageFile(
        @Id @GeneratedValue val id: Long? = null,
        val name: String,
        val type: String? = null,
        @Lob
        val pic: ByteArray
) {
  constructor() : this(-1, "", "", byteArrayOf())
}
