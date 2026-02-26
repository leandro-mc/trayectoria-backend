package com.edumora.trayectoria.web.mapper

import com.edumora.trayectoria.infrastructure.persistence.entity.UserEntity
import com.edumora.trayectoria.web.dto.response.UserResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/**
 * @Mapper(componentModel = "spring") -> MapStruct genera la implementación
 * como un @Component de Spring, inyectable con constructor injection.
 *
 * MapStruct lee las propiedades de source y target, y genera el código
 * de mapeo en tiempo de compilación (no reflection - es rápido).
 *
 * @Mapping(target = "role", expression = ...) -> cuando el campo
 * no mapea directamente, usamos una expresión Java/Kotlin.
 * Aquí extraemos el primer rol del Set de roles del usuario.
 */
@Mapper(componentModel = "spring")
interface UserMapper {

    @Mapping(
        target = "role",
        expression = "java(user.getRoles().stream().findFirst().map(r -> r.getName().replace(\"ROLE_\", \"\")).orElse(\"UNKNOWN\"))"
    )
    fun toResponse(user: UserEntity): UserResponse
}