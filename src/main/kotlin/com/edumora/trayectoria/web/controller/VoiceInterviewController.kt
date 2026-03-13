package com.edumora.trayectoria.web.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Voice controller placeholder — Phase 2.
 *
 * This endpoint exists so the frontend can detect
 * if the feature is enabled (via backend feature flag).
 *
 * When Phase 2 is implemented:
 * - POST /v1/interviews/{id}/voice/start   -> starts WebRTC session
 * - POST /v1/interviews/{id}/voice/message -> sends audio, receives audio
 * - POST /v1/interviews/{id}/voice/end     -> closes session
 */
@RestController
@RequestMapping("/v1/interviews/voice")
@PreAuthorize("hasRole('CANDIDATE')")
@Tag(name = "Interview Voice")
class VoiceInterviewController {

    /**
     * Feature flag — the frontend queries this to decide whether to show the voice button.
     * Once Phase 2 is ready, it returns {"available": true}.
     */
    @GetMapping("/status")
    fun status(): ResponseEntity<Map<String, Any>> =
        ResponseEntity.ok(mapOf(
            "available"   to false,
            "message"     to "Voice interviews coming soon",
            "plannedDate" to "Phase 2"
        ))
}