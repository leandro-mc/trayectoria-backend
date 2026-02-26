package com.edumora.trayectoria.shared.exception

/**
 * Application exception hierarchy.
 *
 * All inherit from RuntimeException (unchecked) — since Kotlin does not have
 * checked exceptions, this is consistent with the language.
 *
 * Each class semantically maps to a specific HTTP status code.
 * The GlobalExceptionHandler handles this mapping.
 *
 * Why separate classes instead of an enum or a code in the constructor:
 * - The exception type IS the information — no need to inspect any internals.
 * - Spring's @ExceptionHandler works by type.
 * - Better readability in use cases: throw NotFoundException("User not found").
 */

// 400 — El cliente envió datos inválidos (lógica de negocio, no validación de campo)
class BadRequestException(message: String) : RuntimeException(message)

// 401 — No autenticado o credenciales inválidas
class UnauthorizedException(message: String) : RuntimeException(message)

// 403 — Autenticado pero sin permiso para este recurso
class ForbiddenException(message: String) : RuntimeException(message)

// 404 — Recurso no encontrado
class NotFoundException(message: String) : RuntimeException(message)

// 409 — Conflicto con el estado actual (email duplicado, postulación duplicada, etc.)
class ConflictException(message: String) : RuntimeException(message)

// 422 — Entidad no procesable (datos válidos en formato pero inválidos en lógica)
// Ej: fecha de fin anterior a fecha de inicio, oferta expirada al postularse
class UnprocessableEntityException(message: String) : RuntimeException(message)

// 500 — Error interno inesperado (no debería usarse directamente — es el fallback)
class InternalException(message: String) : RuntimeException(message)