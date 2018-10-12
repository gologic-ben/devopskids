package com.gologic.devopskids

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@SpringBootApplication // -- Annotation includes @Configuration which force not final modifier function @see: http://engineering.pivotal.io/post/spring-boot-application-with-kotlin/
open class Application : WebMvcConfigurer

fun main(args: Array<String>) {
  runApplication<Application>(*args) {
  }

  @Bean
  fun localeResolver() = CookieLocaleResolver()

  @Bean
  fun localeChangeInterceptor(): LocaleChangeInterceptor {
    val lci = LocaleChangeInterceptor()
    lci.setParamName("lang")
    return lci
  }

  @Override
  fun addInterceptors(registry: InterceptorRegistry) = registry.addInterceptor(localeChangeInterceptor())

}

/****
 * SPRING SECURITY: "/" for public
                    "/api" is private for authenticated users
 *                  Credentials are defined in application.yml
 ****/
@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {
  override fun configure(http: HttpSecurity): Unit {
    // http.httpBasic().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable().authorizeRequests().antMatchers("/web/**").permitAll().anyRequest().authenticated();
    http.httpBasic()
      .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and().csrf().disable()
      .authorizeRequests()
        .antMatchers("/api/**").authenticated()
        .anyRequest().permitAll();
  }
}

/****
 * Exceptions handler
 *     RestExceptionHandler: Catch all exception coming from @RestController
 *     WebExceptionHandler: Catch all exception coming from @Controller
 ****/
@ControllerAdvice(annotations = [RestController::class])
class RestExceptionHandler : ResponseEntityExceptionHandler() {

  @ExceptionHandler(NoSuchElementException::class)
  fun handleNotFound(exception: NoSuchElementException, request: WebRequest): ResponseEntity<Any> {
    return handleExceptionInternal(exception, HttpStatus.NOT_FOUND.value(), HttpHeaders(), HttpStatus.NOT_FOUND, request)
  }

  @ExceptionHandler
  fun handleError(exception: Exception, request: WebRequest): ResponseEntity<Any> {
    return handleExceptionInternal(exception, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request)
  }
}

// TODO
@ControllerAdvice
class WebExceptionHandler {

  @ExceptionHandler(Exception::class)
  fun handleError(exception: Exception): ModelAndView {
    exception.printStackTrace()
    return ModelAndView("error");
  }

  @ExceptionHandler(NoHandlerFoundException::class)
  fun handleError404(exception: Exception): ModelAndView {
    exception.printStackTrace()
    return ModelAndView("404");
  }
}