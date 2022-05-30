package com.walletapp.infrastructure.api

import com.walletapp.domain.DomainException
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler(private val messageSource: MessageSource) {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun handleGenericException(exception: Exception): ResponseEntity<ErrorDTO> {
        logger.error("exception occurring", exception)

        val detail = ErrorDetailDTO(
            "service.generic.error",
            exception.message.orEmpty(),
            "service.generic.error"
        )

        val errorDTO = ErrorDTO(
            exception = "ServiceGenericException",
            errors = listOf(detail)
        )
        return ResponseEntity(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(exception: DomainException): ResponseEntity<ErrorDTO> {
        logger.error("domain exception occurring", exception)
        val detail = ErrorDetailDTO(
            exception.message.orEmpty(),
            getMessage(exception.message.orEmpty(), exception.messageArgs, exception.message),
            exception.message.orEmpty()
        )

        val errorDTO = ErrorDTO(
            exception = exception.javaClass.simpleName,
            errors = listOf(detail)
        )
        return ResponseEntity(errorDTO, HttpStatus.BAD_REQUEST)
    }

    private fun getMessage(key: String, args: Array<out Any>, defaultMessage: String?): String {
        return messageSource.getMessage(key, args, defaultMessage, LocaleContextHolder.getLocale()).orEmpty()
    }
}

data class ErrorDTO(
    val timestamp: Long = Instant.now().toEpochMilli(),
    val exception: String,
    val errors: List<ErrorDetailDTO>
)

data class ErrorDetailDTO(
    var key: String,
    val message: String,
    val errorCode: String
)