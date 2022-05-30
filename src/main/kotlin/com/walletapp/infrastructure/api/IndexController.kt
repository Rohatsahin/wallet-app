package com.walletapp.infrastructure.api

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import springfox.documentation.annotations.ApiIgnore

@RestController
@ApiIgnore
@RequestMapping("/")
class IndexController {

    @ApiIgnore
    @GetMapping
    fun redirectToSwaggerUi(exchange: ServerWebExchange): Mono<Void> {
        val response: ServerHttpResponse = exchange.response
        response.statusCode = HttpStatus.PERMANENT_REDIRECT
        response.headers.add(HttpHeaders.LOCATION, "/swagger-ui/index.html")
        return response.setComplete()
    }
}