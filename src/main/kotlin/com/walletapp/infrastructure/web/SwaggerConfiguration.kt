package com.walletapp.infrastructure.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType.SWAGGER_2
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SwaggerConfiguration {

    @Bean
    fun stockApi(): Docket {
        return Docket(SWAGGER_2)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            .groupName("Wallet Application")
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.walletapp.infrastructure.api"))
            .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title("wallet-service")
            .description("bitcoin wallet management service")
            .contact(contact())
            .version("1.0")
            .build()
    }

    private fun contact(): Contact {
        return Contact("Wallet Management Service", null, "rohatsahin92@gmail.com")
    }
}