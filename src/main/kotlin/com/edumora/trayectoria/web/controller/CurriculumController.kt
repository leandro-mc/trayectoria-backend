package com.edumora.trayectoria.web.controller

import com.edumora.trayectoria.application.usecase.ai.GenerateCurriculumUseCase
import com.edumora.trayectoria.application.usecase.ai.GetBaseCurriculumUseCase
import com.edumora.trayectoria.application.usecase.ai.GetCurriculaUseCase
import com.edumora.trayectoria.shared.util.SecurityUtils
import com.edumora.trayectoria.web.dto.request.ai.GenerateCurriculumRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/curricula")
@PreAuthorize("hasRole('CANDIDATE')")
class CurriculumController(
    private val generateUseCase: GenerateCurriculumUseCase,
    private val getCurriculaUseCase: GetCurriculaUseCase,
    private val getBaseCurriculumUseCase: GetBaseCurriculumUseCase
) {
    @PostMapping("/generate")
    fun generate(@RequestBody @Valid request: GenerateCurriculumRequest) =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(generateUseCase.execute(SecurityUtils.currentUserEmail(), request))

    @GetMapping
    fun listAll() =
        ResponseEntity.ok(getCurriculaUseCase.listAll(SecurityUtils.currentUserEmail()))

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'COMPANY')")
    fun getById(@PathVariable id: Long) =
        ResponseEntity.ok(getCurriculaUseCase.getById(SecurityUtils.currentUserEmail(), id))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        getCurriculaUseCase.delete(SecurityUtils.currentUserEmail(), id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/latest")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'COMPANY')")
    fun getLatestByOffer(
        @RequestParam candidateId: Long,
        @RequestParam offerId: Long
    ) = ResponseEntity.ok(
        getCurriculaUseCase.getLatestByOfferAndCandidate(
            SecurityUtils.currentUserEmail(),
            candidateId,
            offerId
        )
    )

    @GetMapping("/base/{candidateId}")
    @PreAuthorize("hasRole('COMPANY')")
    fun getBase(@PathVariable candidateId: Long) =
        ResponseEntity.ok(
            getBaseCurriculumUseCase.execute(
                SecurityUtils.currentUserEmail(),
                candidateId
            )
        )
}