package com.edumora.trayectoria.auth

import com.edumora.trayectoria.BaseIntegrationTest
import com.edumora.trayectoria.extractField
import com.edumora.trayectoria.toJson
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

@DisplayName("Auth API")
class AuthIntegrationTest : BaseIntegrationTest() {

    @Nested
    @DisplayName("POST api/v1/auth/register/candidate")
    inner class RegisterCandidate {

        @Test
        @DisplayName("registers a new candidate and returns JWT tokens")
        fun `register candidate - success`() {
            registerCandidate(
                email     = "leandro@test.com",
                firstName = "Leandro",
                lastName  = "Mora"
            ).also { result ->
                with(result.response) {
                    assert(status == 201) { "Expected 201, got $status" }
                }
            }

            mockMvc.post("/v1/auth/register/candidate") {
                contentType = MediaType.APPLICATION_JSON
                content = mapOf(
                    "email"     to "leandro2@test.com",
                    "password"  to "password123",
                    "firstName" to "Leandro",
                    "lastName"  to "Mora"
                ).toJson(objectMapper)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.accessToken")  { isNotEmpty() }
                jsonPath("$.refreshToken") { isNotEmpty() }
                jsonPath("$.email")        { value("leandro2@test.com") }
                jsonPath("$.role")         { value("CANDIDATE") }
            }
        }

        @Test
        @DisplayName("returns 409 when email is already registered")
        fun `register candidate - duplicate email`() {
            val email = "duplicate@test.com"
            registerCandidate(email)

            mockMvc.post("/v1/auth/register/candidate") {
                contentType = MediaType.APPLICATION_JSON
                content = mapOf(
                    "email"     to email,
                    "password"  to "password123",
                    "firstName" to "Test",
                    "lastName"  to "User"
                ).toJson(objectMapper)
            }.andExpect {
                status { isConflict() }
                jsonPath("$.status")  { value(409) }
            }
        }

        @Test
        @DisplayName("returns 400 when fields are invalid")
        fun `register candidate - validation failure`() {
            mockMvc.post("/v1/auth/register/candidate") {
                contentType = MediaType.APPLICATION_JSON
                content = mapOf(
                    "email"     to "not-a-valid-email",
                    "password"  to "short",
                    "firstName" to "",
                    "lastName"  to "Mora"
                ).toJson(objectMapper)
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.status")  { value(400) }
                jsonPath("$.details") { isArray() }
            }
        }
    }

    @Nested
    @DisplayName("POST /v1/auth/register/company")
    inner class RegisterCompany {

        @Test
        @DisplayName("registers a new company and returns JWT tokens")
        fun `register company - success`() {
            mockMvc.post("/v1/auth/register/company") {
                contentType = MediaType.APPLICATION_JSON
                content = mapOf(
                    "email"       to "techcorp@test.com",
                    "password"    to "password123",
                    "companyName" to "TechCorp"
                ).toJson(objectMapper)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.accessToken")  { isNotEmpty() }
                jsonPath("$.refreshToken") { isNotEmpty() }
                jsonPath("$.role")         { value("COMPANY") }
            }
        }
    }

    @Nested
    @DisplayName("POST /v1/auth/login")
    inner class Login {

        @Test
        @DisplayName("returns tokens with valid credentials")
        fun `login - success`() {
            val email = "logintest@test.com"
            registerCandidate(email)

            mockMvc.post("/v1/auth/login") {
                contentType = MediaType.APPLICATION_JSON
                content = mapOf(
                    "email"    to email,
                    "password" to "password123"
                ).toJson(objectMapper)
            }.andExpect {
                status { isOk() }
                jsonPath("$.accessToken")  { isNotEmpty() }
                jsonPath("$.refreshToken") { isNotEmpty() }
                jsonPath("$.role")         { value("CANDIDATE") }
            }
        }

        @Test
        @DisplayName("returns 401 with wrong password")
        fun `login - wrong password`() {
            val email = "wrongpass@test.com"
            registerCandidate(email)

            mockMvc.post("/v1/auth/login") {
                contentType = MediaType.APPLICATION_JSON
                content = mapOf(
                    "email"    to email,
                    "password" to "wrongpassword"
                ).toJson(objectMapper)
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.status") { value(401) }
            }
        }

        @Test
        @DisplayName("returns 401 with non-existent email")
        fun `login - non-existent user`() {
            mockMvc.post("/v1/auth/login") {
                contentType = MediaType.APPLICATION_JSON
                content = mapOf(
                    "email"    to "nobody@test.com",
                    "password" to "password123"
                ).toJson(objectMapper)
            }.andExpect {
                status { isUnauthorized() }
            }
        }
    }

    @Nested
    @DisplayName("POST /v1/auth/refresh")
    inner class RefreshToken {

        @Test
        @DisplayName("returns new tokens with valid refresh token")
        fun `refresh - success`() {
            val result = registerCandidate("refresh@test.com")
            val refreshToken = result.extractField(objectMapper, "refreshToken")

            mockMvc.post("/v1/auth/refresh") {
                contentType = MediaType.APPLICATION_JSON
                content = mapOf("refreshToken" to refreshToken).toJson(objectMapper)
            }.andExpect {
                status { isOk() }
                jsonPath("$.accessToken")  { isNotEmpty() }
                jsonPath("$.refreshToken") { isNotEmpty() }
            }
        }

        @Test
        @DisplayName("returns 401 with invalid refresh token")
        fun `refresh - invalid token`() {
            mockMvc.post("/v1/auth/refresh") {
                contentType = MediaType.APPLICATION_JSON
                content = mapOf("refreshToken" to "this.is.not.valid").toJson(objectMapper)
            }.andExpect {
                status { isUnauthorized() }
            }
        }
    }
}