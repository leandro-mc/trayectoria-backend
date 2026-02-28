package com.edumora.trayectoria.shared.util

import com.edumora.trayectoria.shared.exception.NotFoundException
import java.util.Optional

/**
 * Extension function para Optional — evita el .orElseThrow() verboso.
 *
 * Uso:
 *   val user = userRepository.findById(id).orThrow("User not found: $id")
 */
fun <T> Optional<T>.orThrow(message: String): T =
    orElseThrow { NotFoundException(message) }