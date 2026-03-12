package com.edumora.trayectoria

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.post
import com.fasterxml.jackson.databind.ObjectMapper
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Clase base para todos los integration tests.
 *
 * Un único contenedor de PostgreSQL se levanta para TODOS los tests
 * (companion object con @Container) — más rápido que levantar uno por clase.
 *
 * @DirtiesContext(classMode = AFTER_EACH_TEST_CLASS) limpia el contexto
 * de Spring entre clases de test para evitar contaminación de datos.
 *
 * IMPORTANTE: Docker debe estar corriendo antes de ejecutar los tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
abstract class BaseIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
            withDatabaseName("trayectoria_test")
            withUsername("test")
            withPassword("test")
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    // ── Helpers compartidos disponibles para todas las subclases ──

    fun registerCandidate(
        email: String,
        password: String = "password123",
        firstName: String = "Test",
        lastName: String = "User"
    ): MvcResult =
        mockMvc.post("/v1/auth/register/candidate") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                mapOf(
                    "email"     to email,
                    "password"  to password,
                    "firstName" to firstName,
                    "lastName"  to lastName
                )
            )
        }.andReturn()

    fun registerCompany(
        email: String,
        password: String = "password123",
        companyName: String = "Test Corp"
    ): MvcResult =
        mockMvc.post("/v1/auth/register/company") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                mapOf(
                    "email"       to email,
                    "password"    to password,
                    "companyName" to companyName
                )
            )
        }.andReturn()

    /** Registra un candidato y retorna solo el accessToken */
    fun candidateToken(email: String = "candidate-${System.nanoTime()}@test.com"): String {
        val result = registerCandidate(email)
        return result.extractField(objectMapper, "accessToken")
    }

    /** Registra una empresa y retorna solo el accessToken */
    fun companyToken(email: String = "company-${System.nanoTime()}@test.com"): String {
        val result = registerCompany(email)
        return result.extractField(objectMapper, "accessToken")
    }
}
