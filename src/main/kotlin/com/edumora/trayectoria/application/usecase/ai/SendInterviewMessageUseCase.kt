package com.edumora.trayectoria.application.usecase.ai

import com.edumora.trayectoria.application.port.output.AiGenerationPort
import com.edumora.trayectoria.infrastructure.ai.prompts.InterviewPrompts
import com.edumora.trayectoria.infrastructure.persistence.entity.InterviewMessageEntity
import com.edumora.trayectoria.infrastructure.persistence.repository.InterviewMessageRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.SimulatedInterviewRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ForbiddenException
import com.edumora.trayectoria.shared.exception.UnprocessableEntityException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.ai.SendMessageRequest
import com.edumora.trayectoria.web.dto.response.InterviewMessageResponse
import com.edumora.trayectoria.web.mapper.InterviewMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SendInterviewMessageUseCase(
    private val userRepository: UserRepository,
    private val interviewRepository: SimulatedInterviewRepository,
    private val messageRepository: InterviewMessageRepository,
    private val aiPort: AiGenerationPort,
    private val mapper: InterviewMapper
) {
    // Después de N mensajes del usuario, se indica al modelo que cierre la entrevista
    private val maxUserMessages = 8

    @Transactional
    fun execute(
        email: String,
        interviewId: Long,
        request: SendMessageRequest
    ): InterviewMessageResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val interview = interviewRepository.findByIdWithMessages(interviewId, user.id)
            .orThrow("Interview not found: $interviewId")

        if (interview.candidate?.userId != user.id) {
            throw ForbiddenException("You don't own this interview")
        }
        if (interview.status == "COMPLETED") {
            throw UnprocessableEntityException("This interview is already completed")
        }

        // 1. Guardar mensaje del candidato
        messageRepository.save(
            InterviewMessageEntity(
                interview = interview,
                role      = "USER",
                content   = request.content
            )
        )

        // 2. Construir el historial de conversación (excluimos mensajes SYSTEM del historial visible)
        val conversationHistory = interview.messages
            .filter { it.role != "SYSTEM" }
            .joinToString("\n") { "[${it.role}]: ${it.content}" }

        // 3. Obtener el resumen de la oferta del mensaje SYSTEM
        val jobSummary = interview.messages
            .firstOrNull { it.role == "SYSTEM" }?.content ?: ""

        // 4. Contar mensajes del usuario para saber si es la última pregunta
        val userMessageCount = interview.messages.count { it.role == "USER" } + 1
        val isLastQuestion = userMessageCount >= maxUserMessages

        // 5. Generar respuesta del entrevistador
        val aiResponse = aiPort.generate(
            systemPrompt = InterviewPrompts.systemPrompt(jobSummary),
            userPrompt   = InterviewPrompts.interactionPrompt(
                conversationHistory = conversationHistory,
                candidateMessage    = request.content,
                isLastQuestion      = isLastQuestion
            )
        )

        // 6. Guardar y retornar la respuesta del entrevistador
        val assistantMessage = messageRepository.save(
            InterviewMessageEntity(
                interview = interview,
                role      = "ASSISTANT",
                content   = aiResponse
            )
        )

        return mapper.toMessageResponse(assistantMessage)
    }
}