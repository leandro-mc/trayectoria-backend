package com.edumora.trayectoria.web.controller

import com.edumora.trayectoria.application.usecase.application.ApplyToJobOfferUseCase
import com.edumora.trayectoria.application.usecase.application.DeleteApplicationUseCase
import com.edumora.trayectoria.application.usecase.application.GetJobApplicationsUseCase
import com.edumora.trayectoria.application.usecase.application.GetMyApplicationsUseCase
import com.edumora.trayectoria.application.usecase.application.UpdateApplicationStatusUseCase
import com.edumora.trayectoria.shared.util.SecurityUtils
import com.edumora.trayectoria.web.dto.request.application.ApplyRequest
import com.edumora.trayectoria.web.dto.request.application.UpdateApplicationStatusRequest
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * CANDIDATE:
 * POST   /v1/job-offers/{id}/apply   -> apply to offer
 * GET    /v1/applications/mine        -> my applications
 * DELETE /v1/applications/{id}        -> withdraw application
 *
 * COMPANY:
 * GET    /v1/job-offers/{id}/applications         -> applications for their offer
 * PATCH  /v1/applications/{id}/status             -> change status
 */
@RestController
class ApplicationController(
    private val applyUseCase: ApplyToJobOfferUseCase,
    private val getMyApplicationsUseCase: GetMyApplicationsUseCase,
    private val deleteApplicationUseCase: DeleteApplicationUseCase,
    private val getJobApplicationsUseCase: GetJobApplicationsUseCase,
    private val updateStatusUseCase: UpdateApplicationStatusUseCase
) {

    //  Candidato 

    @PostMapping("/v1/job-offers/{jobOfferId}/apply")
    @PreAuthorize("hasRole('CANDIDATE')")
    fun apply(
        @PathVariable jobOfferId: Long,
        @RequestBody request: ApplyRequest = ApplyRequest()
    ): ResponseEntity<*> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(applyUseCase.execute(SecurityUtils.currentUserEmail(), jobOfferId, request))

    @GetMapping("/v1/applications/mine")
    @PreAuthorize("hasRole('CANDIDATE')")
    fun getMyApplications(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ) = ResponseEntity.ok(
        getMyApplicationsUseCase.execute(
            SecurityUtils.currentUserEmail(),
            PageRequest.of(page, size, Sort.by("appliedAt").descending())
        )
    )

    @DeleteMapping("/v1/applications/{id}")
    @PreAuthorize("hasRole('CANDIDATE')")
    fun deleteApplication(@PathVariable id: Long): ResponseEntity<Void> {
        deleteApplicationUseCase.execute(SecurityUtils.currentUserEmail(), id)
        return ResponseEntity.noContent().build()
    }

    //  Empresa 

    @GetMapping("/v1/job-offers/{jobOfferId}/applications")
    @PreAuthorize("hasRole('COMPANY')")
    fun getOfferApplications(
        @PathVariable jobOfferId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ) = ResponseEntity.ok(
        getJobApplicationsUseCase.execute(
            SecurityUtils.currentUserEmail(),
            jobOfferId,
            PageRequest.of(page, size, Sort.by("appliedAt").descending())
        )
    )

    @PatchMapping("/v1/applications/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody @Valid request: UpdateApplicationStatusRequest
    ) = ResponseEntity.ok(
        updateStatusUseCase.execute(SecurityUtils.currentUserEmail(), id, request)
    )
}