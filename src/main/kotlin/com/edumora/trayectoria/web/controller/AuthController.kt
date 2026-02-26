package com.edumora.trayectoria.web.controller

import com.edumora.trayectoria.application.usecase.LoginUseCase
import com.edumora.trayectoria.application.usecase.RefreshTokenUseCase
import com.edumora.trayectoria.application.usecase.RegisterCandidateUseCase
import com.edumora.trayectoria.application.usecase.RegisterCompanyUseCase
import com.edumora.trayectoria.web.dto.request.LoginRequest
import com.edumora.trayectoria.web.dto.request.RefreshTokenRequest
import com.edumora.trayectoria.web.dto.request.RegisterCandidateRequest
import com.edumora.trayectoria.web.dto.request.RegisterCompanyRequest
import com.edumora.trayectoria.web.dto.response.AuthResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @RestController -> combines @Controller + @ResponseBody.
 * All methods automatically return JSON.
 * @RequestMapping -> base prefix for all endpoints in this controller.
 * With context-path=/api in application.yml, the full route
 * becomes: /api/v1/auth/...
 *
 * The controller contains NO business logic — its only roles are:
 * 1. Receive the request.
 * 2. Validate using @Valid.
 * 3. Delegate to the use case.
 * 4. Return the HTTP response.
 */
@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val registerCandidateUseCase: RegisterCandidateUseCase,
    private val registerCompanyUseCase: RegisterCompanyUseCase,
    private val loginUseCase: LoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase
) {

    /**
     * @Valid -> triggers the DTO's validation annotations (@NotBlank, @Email, etc.).
     * If validation fails, the GlobalExceptionHandler catches the error
     * and returns 400 with the list of invalid fields.
     *
     * ResponseEntity<T> -> provides full control over the HTTP status.
     * 201 Created is the correct code for resource creation.
     */
    @PostMapping("/register/candidate")
    fun registerCandidate(
        @RequestBody @Valid request: RegisterCandidateRequest
    ): ResponseEntity<AuthResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(registerCandidateUseCase.execute(request))

    @PostMapping("/register/company")
    fun registerCompany(
        @RequestBody @Valid request: RegisterCompanyRequest
    ): ResponseEntity<AuthResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(registerCompanyUseCase.execute(request))

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid request: LoginRequest
    ): ResponseEntity<AuthResponse> =
        ResponseEntity.ok(loginUseCase.execute(request))

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody @Valid request: RefreshTokenRequest
    ): ResponseEntity<AuthResponse> =
        ResponseEntity.ok(refreshTokenUseCase.execute(request))
}
