package com.edumora.trayectoria.web.dto.request.candidate

import jakarta.validation.constraints.NotEmpty

data class SkillsRequest(
    @field:NotEmpty
    val skillIds: List<Long>
)