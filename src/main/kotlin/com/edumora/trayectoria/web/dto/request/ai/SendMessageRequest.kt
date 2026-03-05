package com.edumora.trayectoria.web.dto.request.ai

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SendMessageRequest(
    @field:NotBlank(message = "Message content is required")
    @field:Size(max = 2000, message = "Message cannot exceed 2000 characters")
    val content: String
)