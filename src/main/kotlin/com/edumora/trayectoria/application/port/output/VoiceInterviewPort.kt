package com.edumora.trayectoria.application.port.output

/**
 * Port for real-time voice interviews — Phase 2.
 *
 * STATUS: Defined but NOT implemented. The actual implementation
 * will follow later.
 *
 * - Clean Architecture requires the use case to depend on the interface,
 * not the implementation.
 * - By defining it now, when Phase 2 arrives, we only need to create the
 * implementation in infrastructure/ai/provider/ and register it
 * as a @Component — use cases remain unchanged.
 *
 * Planned Phase 2 Flow:
 * 1. Candidate starts the voice interview on the frontend.
 * 2. Frontend captures audio via WebRTC.
 * 3. Audio stream reaches the backend via WebSocket.
 * 4. Backend transcribes it (Deepgram or Whisper) and sends it to the AI.
 * 5. AI responds in text → backend converts it to audio (TTS).
 * 6. Audio response is sent back to the frontend via WebSocket.
 */
interface VoiceInterviewPort {

    /**
     * Transcribes audio to text.
     * @param audioBytes  Raw audio bytes (WebM, WAV, etc.)
     * @param language    ISO 639-1 language code, e.g., "es", "en"
     * @return            Transcribed text
     */
    fun transcribeAudio(audioBytes: ByteArray, language: String = "es"): String

    /**
     * Converts text to audio (Text-to-Speech).
     * @param text    Text to convert
     * @param voice   Provider's voice identifier
     * @return        Audio bytes (MP3 or WAV)
     */
    fun textToSpeech(text: String, voice: String = "alloy"): ByteArray

    /**
     * Generates an interview response in streaming mode.
     * Emits tokens as they arrive to reduce perceived latency.
     * kotlinx.coroutines.flow.Flow allows emitting incremental results.
     */
    // fun generateStreamingResponse(
    //     systemPrompt: String,
    //     conversationHistory: List<ConversationMessage>
    // ): kotlinx.coroutines.flow.Flow<String>
}