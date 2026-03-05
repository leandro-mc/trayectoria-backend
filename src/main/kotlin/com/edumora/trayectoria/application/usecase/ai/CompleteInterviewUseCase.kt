package com.edumora.trayectoria.application.usecase.ai

import com.edumora.trayectoria.application.port.output.AiGenerationPort
import com.edumora.trayectoria.infrastructure.ai.prompts.InterviewPrompts
import com.edumora.trayectoria.infrastructure.persistence.repository.SimulatedInterviewRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ForbiddenException
import com.edumora.trayectoria.shared.exception.InternalException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.response.CompletedInterviewResponse
import com.edumora.trayectoria.web.dto.response.InterviewFeedback
import com.edumora.trayectoria.web.mapper.InterviewMapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CompleteInterviewUseCase(
    private val userRepository: UserRepository,
    private val interviewRepository: SimulatedInterviewRepository,
    private val aiPort: AiGenerationPort,
    private val objectMapper: ObjectMapper,
    private val mapper: InterviewMapper
) {
    @Transactional
    fun execute(email: String, interviewId: Long): CompletedInterviewResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val interview = interviewRepository.findByIdWithMessages(interviewId, user.id)
            .orThrow("Interview not found: $interviewId")

        if (interview.candidate?.userId != user.id) {
            throw ForbiddenException("You don't own this interview")
        }

        // Historial completo para generar el feedback (sin mensajes SYSTEM)
        val conversationHistory = interview.messages
            .filter { it.role != "SYSTEM" }
            .joinToString("\n") { "[${it.role}]: ${it.content}" }

        val jobSummary = interview.messages
            .firstOrNull { it.role == "SYSTEM" }?.content ?: ""

        // Generar feedback estructurado en JSON
        val feedbackJson = aiPort.generateJson(
            systemPrompt = InterviewPrompts.systemPrompt(jobSummary),
            userPrompt   = InterviewPrompts.feedbackPrompt(conversationHistory)
        )

        val feedback = runCatching {
            objectMapper.readValue(feedbackJson, InterviewFeedback::class.java)
        }.getOrElse {
            throw InternalException("Could not parse interview feedback: ${it.message}")
        }

        // Marcar la entrevista como completada
        interview.status = "COMPLETED"
        interview.completedAt = java.time.LocalDateTime.now()
        interviewRepository.save(interview)

        return CompletedInterviewResponse(
            interview = mapper.toDetailResponse(interview, interview.messages.filter { it.role != "SYSTEM" }),
            feedback  = feedback
        )
    }
}