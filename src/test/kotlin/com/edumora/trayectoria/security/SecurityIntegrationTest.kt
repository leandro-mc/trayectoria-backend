package com.edumora.trayectoria.security

import com.edumora.trayectoria.BaseIntegrationTest
import com.edumora.trayectoria.toJson
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

/**
 * Verifica que las reglas de acceso están correctamente configuradas.
 *
 * Estos tests son los más valiosos en una revisión de código porque
 * demuestran que la seguridad no es solo configuración — está probada.
 */
@DisplayName("Security — Access Control")
class SecurityIntegrationTest : BaseIntegrationTest() {

    @Test
    @DisplayName("protected endpoint returns 403 without token")
    fun `no token - returns 403`() {
        mockMvc.get("/v1/candidates/me")
            .andExpect { status { isForbidden() } }
    }

    @Test
    @DisplayName("malformed Bearer token returns 403")
    fun `malformed token - returns 403`() {
        mockMvc.get("/v1/candidates/me") {
            header("Authorization", "Bearer this.is.fake.token")
        }.andExpect { status { isForbidden() } }
    }

    @Test
    @DisplayName("COMPANY token cannot access CANDIDATE endpoints")
    fun `company cannot access candidate endpoints`() {
        val token = companyToken()

        mockMvc.get("/v1/candidates/me") {
            header("Authorization", "Bearer $token")
        }.andExpect { status { isForbidden() } }
    }

    @Test
    @DisplayName("CANDIDATE token cannot access COMPANY endpoints")
    fun `candidate cannot access company endpoints`() {
        val token = candidateToken()

        mockMvc.get("/v1/companies/me") {
            header("Authorization", "Bearer $token")
        }.andExpect { status { isForbidden() } }
    }

    @Test
    @DisplayName("CANDIDATE token cannot create job offers")
    fun `candidate cannot create job offers`() {
        val token = candidateToken()

        mockMvc.post("/v1/job-offers") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf("title" to "Test Offer").toJson(objectMapper)
        }.andExpect { status { isForbidden() } }
    }

    @Test
    @DisplayName("COMPANY token cannot apply to job offers")
    fun `company cannot apply to job offers`() {
        val token = companyToken()

        mockMvc.post("/v1/job-offers/1/apply") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = "{}".toByteArray()
        }.andExpect { status { isForbidden() } }
    }

    @Test
    @DisplayName("public endpoints are accessible without authentication")
    fun `public endpoints - no auth required`() {
        mockMvc.get("/v1/job-offers")
            .andExpect { status { isOk() } }

        mockMvc.get("/v1/skills")
            .andExpect { status { isOk() } }
    }
}