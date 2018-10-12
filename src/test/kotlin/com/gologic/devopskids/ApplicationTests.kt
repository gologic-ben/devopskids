package com.gologic.devopskids

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class ApplicationTests {

  @Autowired
  lateinit var repository: KidRepository

  @Test
  fun contextLoads() {
    println("contextLoads")
    repository.findAll()
    println("contextLoads done")
  }

}
