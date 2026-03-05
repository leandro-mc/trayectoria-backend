package com.edumora.trayectoria.application.usecase.ai

import com.edumora.trayectoria.infrastructure.persistence.repository.SimulatedInterviewRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ForbiddenException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.response.SimulatedInterviewResponse
import com.edumora.trayectoria.web.mapper.InterviewMapper
import org.springframework.stereotype.Service

@Service
class GetInterviewsUseCase(
    private val userRepository: UserRepository,
    private val interviewRepository: SimulatedInterviewRepository,
    private val mapper: InterviewMapper
) {
    fun listAll(email: String): List<SimulatedInterviewResponse> {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        // toSummaryResponse — sin mensajes para no sobrecargar la respuesta del listado
        return interviewRepository.findByCandidateUserId(user.id)
            .map { mapper.toSummaryResponse(it) }
    }

    fun getById(email: String, id: Long): SimulatedInterviewResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val interview = interviewRepository.findByIdWithMessages(id, user.id)
            .orThrow("Interview not found: $id")
        if (interview.candidate?.userId != user.id) {
            throw ForbiddenException("You don't own this interview")
        }
        // Filtramos los mensajes SYSTEM — no deben llegar al frontend
        val filteredMessages = interview.messages.filter { it.role != "SYSTEM" }
        return mapper.toDetailResponse(interview, filteredMessages)
    }
}