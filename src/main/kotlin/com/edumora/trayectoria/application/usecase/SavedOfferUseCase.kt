package com.edumora.trayectoria.application.usecase

import com.edumora.trayectoria.infrastructure.persistence.entity.SavedOfferEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.SavedOfferIdEmbeddable
import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.JobOfferRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.SavedOfferRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ConflictException
import com.edumora.trayectoria.shared.exception.NotFoundException
import com.edumora.trayectoria.shared.util.PageResponse
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.response.JobOfferSummaryResponse
import com.edumora.trayectoria.web.mapper.JobOfferMapper
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Las ofertas guardadas usan @EmbeddedId con SavedOfferIdEmbeddable
 * que tiene candidateUserId + jobOfferId como clave compuesta.
 *
 * Guardar = crear registro con esa PK compuesta.
 * Ya guardado = PK ya existe -> ConflictException.
 * Eliminar = delete by PK compuesta.
 */
@Service
class SavedOfferUseCase(
    private val userRepository: UserRepository,
    private val candidateProfileRepository: CandidateProfileRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val savedOfferRepository: SavedOfferRepository,
    private val jobOfferMapper: JobOfferMapper
) {
    fun listSaved(email: String, pageable: Pageable): PageResponse<JobOfferSummaryResponse> {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        return PageResponse.from(
            savedOfferRepository
                .findJobOffersByCandidateUserId(user.id, pageable)
                .map { jobOfferMapper.toSummaryResponse(it) }
        )
    }

    @Transactional
    fun save(email: String, jobOfferId: Long) {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val candidate = candidateProfileRepository.findByUserId(user.id)
            .orThrow("Candidate profile not found")

        if (!jobOfferRepository.existsById(jobOfferId)) {
            throw NotFoundException("Job offer not found: $jobOfferId")
        }

        if (savedOfferRepository.existsByIdCandidateIdAndIdJobOfferId(user.id, jobOfferId)) {
            throw ConflictException("Job offer already saved")
        }

        val jobOffer = jobOfferRepository.findById(jobOfferId).orThrow("Job offer not found")

        savedOfferRepository.save(
            SavedOfferEntity(
                id       = SavedOfferIdEmbeddable(
                    candidateId = user.id,
                    jobOfferId      = jobOfferId
                ),
                candidate = candidate,
                jobOffer  = jobOffer
            )
        )
    }

    @Transactional
    fun remove(email: String, jobOfferId: Long) {
        val user = userRepository.findByEmail(email).orThrow("User not found")

        if (!savedOfferRepository.existsByIdCandidateIdAndIdJobOfferId(user.id, jobOfferId)) {
            throw NotFoundException("Saved offer not found")
        }

        savedOfferRepository.deleteByCandidateUserIdAndJobOfferId(user.id, jobOfferId)
    }

    fun isSaved(email: String, jobOfferId: Long): Boolean {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        return savedOfferRepository.existsByIdCandidateIdAndIdJobOfferId(user.id, jobOfferId)
    }
}
