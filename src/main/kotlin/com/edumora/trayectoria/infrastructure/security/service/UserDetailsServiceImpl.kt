package com.edumora.trayectoria.infrastructure.security.service

import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Spring Security calls this service during authentication
 * to load the user from the database.
 *
 * UserDetailsService is a Spring Security interface —
 * implementing it is sufficient for the DaoAuthenticationProvider
 * to use it automatically.
 *
 * loadUserByUsername() receives the "username" — in our app, this is the email.
 */
@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmailWithRoles(email)
            .orElseThrow { UsernameNotFoundException("User not found: $email") }

        // Construimos los GrantedAuthority desde roles Y privilegios
        // Spring Security necesita estos para evaluar @PreAuthorize y hasRole()
        val authorities = user.roles.flatMap { role ->
            // Agrega el rol mismo (ROLE_CANDIDATE) + sus privilegios (READ_PRIVILEGE)
            listOf(SimpleGrantedAuthority(role.name)) +
                    role.privileges.map { SimpleGrantedAuthority(it.name) }
        }.toSet()

        // User es la implementación estándar de UserDetails de Spring Security
        return User.builder()
            .username(user.email)
            .password(user.password)
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(!user.enabled)
            .credentialsExpired(user.tokenExpired)
            .disabled(!user.enabled)
            .build()
    }
}