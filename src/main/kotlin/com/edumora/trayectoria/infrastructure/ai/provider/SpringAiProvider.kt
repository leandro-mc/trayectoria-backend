package com.edumora.trayectoria.infrastructure.ai.provider

import com.edumora.trayectoria.application.port.output.AiGenerationPort
import com.edumora.trayectoria.shared.exception.InternalException
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Component

/**
 * Implementation of the AiGenerationPort using Spring AI.
 *
 * Spring AI auto-configures ChatClient.Builder from application.yml
 * (spring.ai.openai.*) without the need for any manual @Bean definition.
 *
 * @Component instead of @Service:
 * This is infrastructure, not business logic.
 * @Service semantically indicates "use case / domain logic."
 *
 * Thread safety:
 * Spring AI's ChatClient is thread-safe — it is a stateless singleton.
 * There are no setStrategy() methods or instance variables that change at runtime.
 */
@Component
class SpringAiProvider(
    chatClientBuilder: ChatClient.Builder
) : AiGenerationPort {

    private val log = LoggerFactory.getLogger(SpringAiProvider::class.java)

    // ChatClient es el punto de entrada de Spring AI — equivale al cliente HTTP
    // que antes construíamos manualmente con RestTemplate.
    // .build() aquí usa la config de application.yml automáticamente.
    private val chatClient: ChatClient = chatClientBuilder.build()

    override fun generate(systemPrompt: String, userPrompt: String): String {
        return runCatching {
            chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content()
                ?: throw InternalException("AI returned empty response")
        }.getOrElse { e ->
            log.error("AI generation failed: ${e.message}", e)
            throw InternalException("AI service unavailable. Please try again later.")
        }
    }

    override fun generateJson(systemPrompt: String, userPrompt: String): String {
        // Agregamos instrucción explícita al system prompt para garantizar JSON puro.
        // Aunque usemos JSON mode en la API, el prompt reforzado reduce errores.
        val jsonSystemPrompt = """
            $systemPrompt
            
            CRITICAL: Respond ONLY with valid JSON. No markdown, no backticks, no explanations.
            Your response must start with { and end with }.
        """.trimIndent()

        return runCatching {
            chatClient.prompt()
                .system(jsonSystemPrompt)
                .user(userPrompt)
                .call()
                .content()
                ?.trim()
                ?.removePrefix("```json")  // Limpieza defensiva por si el modelo ignora las instrucciones
                ?.removePrefix("```")
                ?.removeSuffix("```")
                ?.trim()
                ?: throw InternalException("AI returned empty response")
        }.getOrElse { e ->
            log.error("AI JSON generation failed: ${e.message}", e)
            throw InternalException("AI service unavailable. Please try again later.")
        }
    }
}