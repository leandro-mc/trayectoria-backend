package com.edumora.trayectoria.web.controller

import com.edumora.trayectoria.application.usecase.joboffer.JobOfferUseCase
import com.edumora.trayectoria.application.usecase.SkillCatalogUseCase
import com.edumora.trayectoria.shared.util.SecurityUtils
import com.edumora.trayectoria.web.dto.request.joboffer.CreateJobOfferRequest
import com.edumora.trayectoria.web.dto.request.joboffer.UpdateJobOfferStatusRequest
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/job-offers")
class JobOfferController(
    private val jobOfferUseCase: JobOfferUseCase,
    private val skillCatalogUseCase: SkillCatalogUseCase
) {
    //  Público 

    /**
     * @RequestParam(required = false) -> all filters are optional.
     * If not provided, the use case ignores them and returns all results.
     *
     * page and size with defaults -> automatic pagination.
     * sort=createdAt,desc -> Spring converts this into a Sort object automatically.
     */
    @GetMapping
    fun listActive(
        @RequestParam(required = false) workMode: String?,
        @RequestParam(required = false) jobType: String?,
        @RequestParam(required = false) skillId: Long?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ) = ResponseEntity.ok(
        jobOfferUseCase.listActive(
            workMode, jobType, skillId, keyword,
            PageRequest.of(page, size, Sort.by("createdAt").descending())
        )
    )

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) =
        ResponseEntity.ok(jobOfferUseCase.getById(id))

    //  Empresa 

    @GetMapping("/mine")
    @PreAuthorize("hasRole('COMPANY')")
    fun listMine(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ) = ResponseEntity.ok(
        jobOfferUseCase.listByCompany(
            SecurityUtils.currentUserEmail(),
            PageRequest.of(page, size, Sort.by("createdAt").descending())
        )
    )

    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    fun create(@RequestBody @Valid request: CreateJobOfferRequest) =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(jobOfferUseCase.create(SecurityUtils.currentUserEmail(), request))

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid request: CreateJobOfferRequest
    ) = ResponseEntity.ok(jobOfferUseCase.update(SecurityUtils.currentUserEmail(), id, request))

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    fun changeStatus(
        @PathVariable id: Long,
        @RequestBody @Valid request: UpdateJobOfferStatusRequest
    ) = ResponseEntity.ok(jobOfferUseCase.changeStatus(SecurityUtils.currentUserEmail(), id, request))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        jobOfferUseCase.delete(SecurityUtils.currentUserEmail(), id)
        return ResponseEntity.noContent().build()
    }
}