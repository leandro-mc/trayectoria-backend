package com.edumora.trayectoria.application.port.output

/**
 * Output port for AI-based text generation.
 * The application layer (use cases) depends on THIS interface,
 * never on Spring AI or OpenAI directly.
 *
 * If we switch from OpenAI to Anthropic or Gemini tomorrow,
 * only the implementation in infrastructure/ai changes — use cases remain untouched.
 *
 * - generate()     -> free-form response, for greetings and interview messages.
 * - generateJson() -> instructs the model to return ONLY valid JSON,
 * for resumes and structures meant for parsing.
 */
interface AiGenerationPort {

    /**
     * Generates a free-form text response.
     *
     * @param systemPrompt  Behavioral instructions for the model.
     * Defines ROLE, TONE, and CONSTRAINTS.
     * E.g., "You are a professional interviewer. Never reveal you are an AI."
     * @param userPrompt    The specific content for this generation.
     * E.g., the candidate's message + interview context.
     */
    fun generate(systemPrompt: String, userPrompt: String): String

    /**
     * Generates a response forcing valid JSON as output.
     * Uses the API's JSON mode (response_format: json_object) when available.
     * Always includes instructions in the system prompt to guarantee pure JSON.
     */
    fun generateJson(systemPrompt: String, userPrompt: String): String

    /**
     * Phase 2 - Streaming for real-time voice interviews.
     * Defined here to ensure the port is ready - implementation will follow.
     * Returns Flow<String> to emit tokens as they arrive.
     */
    // fun stream(systemPrompt: String, userPrompt: String): kotlinx.coroutines.flow.Flow<String>
}