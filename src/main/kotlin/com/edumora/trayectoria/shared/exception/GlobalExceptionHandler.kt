package com.edumora.trayectoria.shared.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * @RestControllerAdvice -> intercepts exceptions from ALL controllers.
 * Centralizes error handling — no controller needs try/catch blocks.
 *
 * Handler order: Spring looks for the most specific handler first.
 * If no exact match is found, it traverses up the inheritance hierarchy.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    // Logger para registrar errores inesperados — no loguear errores de cliente (4xx)
    // porque saturan los logs con ruido. Solo loguear errores de servidor (5xx).
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    //  Excepciones de negocio (4xx) ─

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(e: BadRequestException) =
        build(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(e: UnauthorizedException) =
        build(HttpStatus.UNAUTHORIZED, e.message!!)

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(e: ForbiddenException) =
        build(HttpStatus.FORBIDDEN, e.message!!)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(e: NotFoundException) =
        build(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(e: ConflictException) =
        build(HttpStatus.CONFLICT, e.message!!)

    @ExceptionHandler(UnprocessableEntityException::class)
    fun handleUnprocessable(e: UnprocessableEntityException) =
        build(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    //  Spring Security (4xx) ─

    // Spring Security lanza esta cuando hasRole() falla
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException) =
        build(HttpStatus.FORBIDDEN, "You don't have permission to access this resource")

    // AuthenticationManager lanza esta en login con credenciales incorrectas
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(e: BadCredentialsException) =
        build(HttpStatus.UNAUTHORIZED, "Invalid credentials")

    // Usuario con enabled = false
    @ExceptionHandler(DisabledException::class)
    fun handleDisabled(e: DisabledException) =
        build(HttpStatus.UNAUTHORIZED, "Account is disabled")

    // Usuario bloqueado
    @ExceptionHandler(LockedException::class)
    fun handleLocked(e: LockedException) =
        build(HttpStatus.UNAUTHORIZED, "Account is locked")

    //  Validación de request (@Valid) 

    // Spring lanza esta cuando @Valid falla en el @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = e.bindingResult.allErrors.map { error ->
            if (error is FieldError) "${error.field}: ${error.defaultMessage}"
            else error.defaultMessage ?: "Validation error"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                status  = 400,
                error   = "Bad Request",
                message = "Validation failed",
                details = details
            )
        )
    }

    //  Errores de request HTTP ─

    // JSON malformado en el body
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(e: HttpMessageNotReadableException) =
        build(HttpStatus.BAD_REQUEST, "Malformed JSON request body")

    // Query param faltante
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(e: MissingServletRequestParameterException) =
        build(HttpStatus.BAD_REQUEST, "Required parameter missing: ${e.parameterName}")

    // Tipo incorrecto en path variable o query param (ej: /offers/abc en lugar de /offers/123)
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(e: MethodArgumentTypeMismatchException) =
        build(HttpStatus.BAD_REQUEST, "Invalid value '${e.value}' for parameter '${e.name}'")

    // Metodo HTTP no soportado (POST en un endpoint que solo acepta GET)
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(e: HttpRequestMethodNotSupportedException) =
        build(HttpStatus.METHOD_NOT_ALLOWED, "Method ${e.method} not allowed")

    // Ruta no encontrada (diferente a recurso no encontrado — esta es la URL en sí)
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResource(e: NoResourceFoundException) =
        build(HttpStatus.NOT_FOUND, "Endpoint not found")

    //  Fallback (5xx) 

    // Cualquier excepción no manejada — loguear porque es inesperada
    @ExceptionHandler(Exception::class)
    fun handleGeneral(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error: ${e.javaClass.simpleName} — ${e.message}", e)
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")
    }

    //  Builder privado ─

    private fun build(status: HttpStatus, message: String) =
        ResponseEntity.status(status).body(
            ErrorResponse(
                status  = status.value(),
                error   = status.reasonPhrase,
                message = message
            )
        )
}