package com.backend.exceptions

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.sql.SQLException
import java.util.NoSuchElementException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ApplicationSettingsNotFoundException::class)
    fun handleApplicationSettingsNotFound(exception: ApplicationSettingsNotFoundException): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.NOT_FOUND,
        )

    @ExceptionHandler(ApplicationSettingsAlreadyExistsException::class)
    fun handleApplicationSettingsAlreadyExists(exception: ApplicationSettingsAlreadyExistsException): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.CONFLICT,
        )

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElement(exception: NoSuchElementException): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.NOT_FOUND,
        )

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(exception: EntityNotFoundException): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.NOT_FOUND,
        )

    @ExceptionHandler(EmptyResultDataAccessException::class)
    fun handleEmptyResult(exception: EmptyResultDataAccessException): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.NOT_FOUND,
            message = "Resource not found",
        )

    @ExceptionHandler(SQLException::class)
    fun handleSqlException(exception: SQLException): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "Database error",
        )

    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKey(exception: DuplicateKeyException): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.CONFLICT,
            message = "Resource already exists",
        )

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleMediaTypeNotSupported(exception: HttpMediaTypeNotSupportedException): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            message = "Unsupported media type",
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details =
            exception.bindingResult
                .fieldErrors
                .associate { error ->
                    error.field to (error.defaultMessage ?: "Invalid value")
                }

        return buildResponse(
            exception = exception,
            status = HttpStatus.BAD_REQUEST,
            message = "Validation failed",
            details = details,
        )
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(exception: AuthenticationException): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.UNAUTHORIZED,
            message = "Authentication failed",
        )

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(exception: AccessDeniedException): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.FORBIDDEN,
            message = "Access denied",
        )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(exception: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.BAD_REQUEST,
            message = "Malformed request body",
        )

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> =
        buildResponse(
            exception = exception,
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "Internal server error",
        )

    private fun buildResponse(
        exception: Exception,
        status: HttpStatus,
        message: String = exception.message ?: status.reasonPhrase,
        details: Map<String, String>? = null,
    ): ResponseEntity<ErrorResponse> {
        val logBuilder =
            if (status.is5xxServerError) {
                log.atError()
            } else {
                log.atWarn()
            }

        logBuilder
            .addKeyValue("exception", exception::class.simpleName)
            .addKeyValue("status", status.value())
            .addKeyValue("error", status.reasonPhrase)
            .log(
                message,
                if (status.is5xxServerError) exception else null,
            )

        return ResponseEntity
            .status(status)
            .body(
                ErrorResponse(
                    status = status.value(),
                    error = status.reasonPhrase,
                    message = message,
                    details = details,
                ),
            )
    }
}
