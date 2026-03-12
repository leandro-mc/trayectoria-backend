package com.edumora.trayectoria.candidate

import com.edumora.trayectoria.BaseIntegrationTest
import com.edumora.trayectoria.extractLong
import com.edumora.trayectoria.toJson
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@DisplayName("Candidate Profile API")
class CandidateProfileIntegrationTest : BaseIntegrationTest() {

    private lateinit var token: String

    @BeforeEach
    fun setup() {
        // Email único por test — evita conflictos entre tests
        token = candidateToken("profile-${System.nanoTime()}@test.com")
    }

    @Test
    @DisplayName("GET /me returns profile after registration")
    fun `get profile - success`() {
        mockMvc.get("/v1/candidates/me") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
            jsonPath("$.email")           { isNotEmpty() }
            jsonPath("$.skills")          { isArray() }
            jsonPath("$.workExperiences") { isArray() }
            jsonPath("$.educations")      { isArray() }
            jsonPath("$.languages")       { isArray() }
        }
    }

    @Test
    @DisplayName("PUT /me updates profile fields")
    fun `update profile - success`() {
        mockMvc.put("/v1/candidates/me") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = mapOf(
                "bio"         to "Backend developer passionate about clean code",
                "location"    to "Heredia, Costa Rica",
                "linkedinUrl" to "https://linkedin.com/in/leandromora",
                "githubUrl"   to "https://github.com/leandromora"
            ).toJson(objectMapper)
        }.andExpect {
            status { isOk() }
            jsonPath("$.bio")         { value("Backend developer passionate about clean code") }
            jsonPath("$.location")    { value("Heredia, Costa Rica") }
            jsonPath("$.linkedinUrl") { value("https://linkedin.com/in/leandromora") }
        }
    }

    @Nested
    @DisplayName("Work Experience")
    inner class WorkExperience {

        @Test
        @DisplayName("POST adds experience, GET lists it")
        fun `add and list experience`() {
            // Agregar
            val result = mockMvc.post("/v1/candidates/me/experience") {
                header("Authorization", "Bearer $token")
                contentType = MediaType.APPLICATION_JSON
                content = mapOf(
                    "company"     to "Ministerio de Relaciones Exteriores",
                    "position"    to "Software Developer & Scrum Master",
                    "description" to "Developed telework management system",
                    "isCurrent"   to true
                ).toJson(objectMapper)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.id")        { isNumber() }
                jsonPath("$.company")   { value("Ministerio de Relaciones Exteriores") }
                jsonPath("$.isCurrent") { value(true) }
            }.andReturn()

            val expId = result.extractLong(objectMapper, "id")

            // Verificar en el listado
            mockMvc.get("/v1/candidates/me/experience") {
                header("Authorization", "Bearer $token")
            }.andExpect {
                status { isOk() }
                jsonPath("$[0].company") { value("Ministerio de Relaciones Exteriores") }
            }

            // Eliminar
            mockMvc.delete("/v1/candidates/me/experience/$expId") {
                header("Authorization", "Bearer $token")
            }.andExpect {
                status { isNoContent() }
            }

            // Verificar que fue eliminada
            mockMvc.get("/v1/candidates/me/experience") {
                header("Authorization", "Bearer $token")
            }.andExpect {
                status { isOk() }
                jsonPath("$") { isEmpty() }
            }
        }
    }

    @Nested
    @DisplayName("Education")
    inner class Education {

        @Test
        @DisplayName("POST adds education, DELETE removes it")
        fun `add and delete education`() {
            val result = mockMvc.post("/v1/candidates/me/education") {
                header("Authorization", "Bearer $token")
                contentType = MediaType.APPLICATION_JSON
                content = mapOf(
                    "institution"  to "Universidad Nacional de Costa Rica",
                    "degree"       to "Bachillerato",
                    "fieldOfStudy" to "Ingeniería en Sistemas de Información"
                ).toJson(objectMapper)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.institution") { value("Universidad Nacional de Costa Rica") }
            }.andReturn()

            val educationId = result.extractLong(objectMapper, "id")

            mockMvc.delete("/v1/candidates/me/education/$educationId") {
                header("Authorization", "Bearer $token")
            }.andExpect {
                status { isNoContent() }
            }
        }
    }

    @Nested
    @DisplayName("Languages")
    inner class Languages {

        @Test
        @DisplayName("POST adds language, returns 409 on duplicate")
        fun `add language and reject duplicate`() {
            // Agregar idioma
            mockMvc.post("/v1/candidates/me/languages") {
                header("Authorization", "Bearer $token")
                contentType = MediaType.APPLICATION_JSON
                content = mapOf(
                    "language" to "Inglés",
                    "level"    to "B2"
                ).toJson(objectMapper)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.language") { value("Inglés") }
                jsonPath("$.level")    { value("B2") }
            }

            // Duplicado — debe retornar 409
            mockMvc.post("/v1/candidates/me/languages") {
                header("Authorization", "Bearer $token")
                contentType = MediaType.APPLICATION_JSON
                content = mapOf(
                    "language" to "Inglés",
                    "level"    to "C1"
                ).toJson(objectMapper)
            }.andExpect {
                status { isConflict() }
            }
        }
    }
}