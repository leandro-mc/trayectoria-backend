package com.edumora.trayectoria.jobs

import com.edumora.trayectoria.BaseIntegrationTest
import com.edumora.trayectoria.extractField
import com.edumora.trayectoria.extractLong
import com.edumora.trayectoria.toJson
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

/**
 * Test E2E del flujo principal de negocio:
 *   Empresa crea oferta -> Candidato se postula -> Empresa gestiona estado
 *
 * Un solo test que recorre todo el flujo — demuestra que el sistema
 * funciona end-to-end, no solo endpoint por endpoint.
 */
@DisplayName("Job Offer — Full Application Flow")
class JobOfferFlowIntegrationTest : BaseIntegrationTest() {

    @Test
    @DisplayName("company creates offer -> candidate applies -> company manages status")
    fun `full application flow`() {
        val companyTok = companyToken("company@flow-test.com")
        val candidateTok = candidateToken("candidate@flow-test.com")

        //  1. Empresa crea oferta (queda en DRAFT por defecto) 
        val offerResult = mockMvc.post("/v1/job-offers") {
            header("Authorization", "Bearer $companyTok")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf(
                "title"       to "Senior Kotlin Developer",
                "description" to "We need a Kotlin expert for our platform",
                "workMode"    to "REMOTE",
                "jobType"     to "FULL_TIME"
            ).toJson(objectMapper)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.title")  { value("Senior Kotlin Developer") }
        }.andReturn()

        val offerId = offerResult.extractLong(objectMapper, "id")

        //  2. Empresa activa la oferta 
        mockMvc.patch("/v1/job-offers/$offerId/status") {
            header("Authorization", "Bearer $companyTok")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf("status" to "ACTIVE").toJson(objectMapper)
        }.andExpect {
            status { isOk() }
            jsonPath("$.status") { value("ACTIVE") }
        }

        //  3. La oferta aparece en el listado público 
        mockMvc.get("/v1/job-offers") {
            param("keyword", "Kotlin")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content[0].title") { value("Senior Kotlin Developer") }
        }

        //  4. Candidato se postula 
        val applicationResult = mockMvc.post("/v1/job-offers/$offerId/apply") {
            header("Authorization", "Bearer $candidateTok")
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }.andExpect {
            status { isCreated() }
            jsonPath("$.status")     { value("PENDING") }
            jsonPath("$.jobOfferId") { value(offerId) }
        }.andReturn()

        val applicationId = applicationResult.extractLong(objectMapper, "id")

        //  5. No puede postularse dos veces 
        mockMvc.post("/v1/job-offers/$offerId/apply") {
            header("Authorization", "Bearer $candidateTok")
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }.andExpect {
            status { isConflict() }
        }

        //  6. Empresa ve las postulaciones 
        mockMvc.get("/v1/job-offers/$offerId/applications") {
            header("Authorization", "Bearer $companyTok")
        }.andExpect {
            status { isOk() }
            jsonPath("$.totalElements")     { value(1) }
            jsonPath("$.content[0].status") { value("PENDING") }
        }

        //  7. Empresa mueve a IN_REVIEW 
        mockMvc.patch("/v1/applications/$applicationId/status") {
            header("Authorization", "Bearer $companyTok")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf("status" to "IN_REVIEW").toJson(objectMapper)
        }.andExpect {
            status { isOk() }
            jsonPath("$.status") { value("IN_REVIEW") }
        }

        //  8. Candidato ve su postulación actualizada 
        mockMvc.get("/v1/applications/mine") {
            header("Authorization", "Bearer $candidateTok")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content[0].status") { value("IN_REVIEW") }
        }

        //  9. Empresa acepta al candidato 
        mockMvc.patch("/v1/applications/$applicationId/status") {
            header("Authorization", "Bearer $companyTok")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf("status" to "ACCEPTED").toJson(objectMapper)
        }.andExpect {
            status { isOk() }
            jsonPath("$.status") { value("ACCEPTED") }
        }

        //  10. Candidato no puede retirar postulación aceptada 
        mockMvc.delete("/v1/applications/$applicationId") {
            header("Authorization", "Bearer $candidateTok")
        }.andExpect {
            status { isUnprocessableEntity() }
        }
    }

    @Test
    @DisplayName("candidate cannot apply to closed job offer")
    fun `apply to closed offer - returns 422`() {
        val companyTok  = companyToken("company2@flow-test.com")
        val candidateTok = candidateToken("candidate2@flow-test.com")

        // Crear y activar oferta
        val offerResult = mockMvc.post("/v1/job-offers") {
            header("Authorization", "Bearer $companyTok")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf(
                "title"  to "Junior Developer",
                "status" to "ACTIVE"
            ).toJson(objectMapper)
        }.andReturn()
        val offerId = offerResult.extractLong(objectMapper, "id")

        // Activar
        mockMvc.patch("/v1/job-offers/$offerId/status") {
            header("Authorization", "Bearer $companyTok")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf("status" to "ACTIVE").toJson(objectMapper)
        }

        // Empresa cierra la oferta
        mockMvc.patch("/v1/job-offers/$offerId/status") {
            header("Authorization", "Bearer $companyTok")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf("status" to "CLOSED").toJson(objectMapper)
        }.andExpect { status { isOk() } }

        // Candidato intenta postularse — debe fallar con 422
        mockMvc.post("/v1/job-offers/$offerId/apply") {
            header("Authorization", "Bearer $candidateTok")
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }.andExpect {
            status { isUnprocessableEntity() }
        }
    }

    @Test
    @DisplayName("company cannot update status of another company's application")
    fun `company cannot manage other company applications`() {
        val company1Token = companyToken("company3@flow-test.com")
        val company2Token = companyToken("company4@flow-test.com")
        val candidateTok  = candidateToken("candidate3@flow-test.com")

        // Company1 crea y activa una oferta
        val offerResult = mockMvc.post("/v1/job-offers") {
            header("Authorization", "Bearer $company1Token")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf("title" to "Dev Role").toJson(objectMapper)
        }.andReturn()
        val offerId = offerResult.extractLong(objectMapper, "id")

        mockMvc.patch("/v1/job-offers/$offerId/status") {
            header("Authorization", "Bearer $company1Token")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf("status" to "ACTIVE").toJson(objectMapper)
        }

        // Candidato se postula
        val appResult = mockMvc.post("/v1/job-offers/$offerId/apply") {
            header("Authorization", "Bearer $candidateTok")
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }.andReturn()
        val applicationId = appResult.extractLong(objectMapper, "id")

        // Company2 intenta cambiar el estado — debe fallar con 403
        mockMvc.patch("/v1/applications/$applicationId/status") {
            header("Authorization", "Bearer $company2Token")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf("status" to "ACCEPTED").toJson(objectMapper)
        }.andExpect {
            status { isForbidden() }
        }
    }
}
