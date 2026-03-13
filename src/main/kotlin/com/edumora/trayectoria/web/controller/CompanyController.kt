package com.edumora.trayectoria.web.controller

import com.edumora.trayectoria.application.usecase.company.GetCompanyProfileUseCase
import com.edumora.trayectoria.application.usecase.company.GetStatsUseCase
import com.edumora.trayectoria.application.usecase.company.UpdateCompanyProfileUseCase
import com.edumora.trayectoria.shared.util.SecurityUtils
import com.edumora.trayectoria.web.dto.request.company.UpdateCompanyProfileRequest
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/companies")
@PreAuthorize("hasRole('COMPANY')")
@Tag(name = "Company Profile")
class CompanyController(
    private val getProfileUseCase: GetCompanyProfileUseCase,
    private val updateProfileUseCase: UpdateCompanyProfileUseCase,
    private val getStatsUseCase: GetStatsUseCase
) {
    @GetMapping("/me")
    fun getProfile() =
        ResponseEntity.ok(getProfileUseCase.execute(SecurityUtils.currentUserEmail()))

    @PutMapping("/me")
    fun updateProfile(@RequestBody @Valid request: UpdateCompanyProfileRequest) =
        ResponseEntity.ok(updateProfileUseCase.execute(SecurityUtils.currentUserEmail(), request))

    @GetMapping("/me/stats")
    @PreAuthorize("hasRole('COMPANY')")
    fun getStats() =
        ResponseEntity.ok(getStatsUseCase.execute(SecurityUtils.currentUserEmail()))
}