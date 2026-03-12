package com.edumora.trayectoria.saved

import com.edumora.trayectoria.BaseIntegrationTest
import com.edumora.trayectoria.extractLong
import com.edumora.trayectoria.toJson
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@DisplayName("Saved Offers API")
class SavedOffersIntegrationTest : BaseIntegrationTest() {

    private lateinit var candidateTok: String
    private var offerId: Long = 0

    @BeforeEach
    fun setup() {
        candidateTok = candidateToken("saved-${System.nanoTime()}@test.com")
        val companyTok = companyToken("saved-co-${System.nanoTime()}@test.com")

        // Crear y activar una oferta para los tests
        val result = mockMvc.post("/v1/job-offers") {
            header("Authorization", "Bearer $companyTok")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf("title" to "Offer to Save").toJson(objectMapper)
        }.andReturn()
        offerId = result.extractLong(objectMapper, "id")

        mockMvc.patch("/v1/job-offers/$offerId/status") {
            header("Authorization", "Bearer $companyTok")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf("status" to "ACTIVE").toJson(objectMapper)
        }
    }

    @Test
    @DisplayName("save, check and unsave a job offer")
    fun `save and unsave offer`() {
        // Guardar
        mockMvc.post("/v1/saved-offers/$offerId") {
            header("Authorization", "Bearer $candidateTok")
        }.andExpect {
            status { isCreated() }
        }

        // Check — debe estar guardada
        mockMvc.get("/v1/saved-offers/$offerId/check") {
            header("Authorization", "Bearer $candidateTok")
        }.andExpect {
            status { isOk() }
            jsonPath("$.saved") { value(true) }
        }

        // Aparece en el listado
        mockMvc.get("/v1/saved-offers") {
            header("Authorization", "Bearer $candidateTok")
        }.andExpect {
            status { isOk() }
            jsonPath("$.totalElements") { value(1) }
        }

        // Eliminar
        mockMvc.delete("/v1/saved-offers/$offerId") {
            header("Authorization", "Bearer $candidateTok")
        }.andExpect {
            status { isNoContent() }
        }

        // Check — ya no debe estar guardada
        mockMvc.get("/v1/saved-offers/$offerId/check") {
            header("Authorization", "Bearer $candidateTok")
        }.andExpect {
            status { isOk() }
            jsonPath("$.saved") { value(false) }
        }
    }
}