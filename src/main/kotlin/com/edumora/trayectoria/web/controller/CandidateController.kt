package com.edumora.trayectoria.web.controller

import com.edumora.trayectoria.application.usecase.CandidateLanguageUseCase
import com.edumora.trayectoria.application.usecase.CandidateSkillUseCase
import com.edumora.trayectoria.application.usecase.EducationUseCase
import com.edumora.trayectoria.application.usecase.GetCandidateProfileUseCase
import com.edumora.trayectoria.application.usecase.UpdateCandidateProfileUseCase
import com.edumora.trayectoria.application.usecase.WorkExperienceUseCase
import com.edumora.trayectoria.shared.util.SecurityUtils
import com.edumora.trayectoria.web.dto.request.EducationRequest
import com.edumora.trayectoria.web.dto.request.LanguageRequest
import com.edumora.trayectoria.web.dto.request.SkillsRequest
import com.edumora.trayectoria.web.dto.request.UpdateCandidateProfileRequest
import com.edumora.trayectoria.web.dto.request.WorkExperienceRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @PreAuthorize("hasRole('CANDIDATE')") -> double security in addition to SecurityConfig.
 * SecurityConfig blocks at the URL level, while @PreAuthorize blocks at the method level.
 * Using both in combination is the industry standard.
 *
 * SecurityUtils.currentUserEmail() extracts the email from the active JWT token
 * to ensure that each user can only view and edit THEIR OWN profile.
 */
@RestController
@RequestMapping("/v1/candidates")
@PreAuthorize("hasRole('CANDIDATE')")
class CandidateController(
    private val getProfileUseCase: GetCandidateProfileUseCase,
    private val updateProfileUseCase: UpdateCandidateProfileUseCase,
    private val workExperienceUseCase: WorkExperienceUseCase,
    private val educationUseCase: EducationUseCase,
    private val skillUseCase: CandidateSkillUseCase,
    private val languageUseCase: CandidateLanguageUseCase
) {
    //  Profile ─
    @GetMapping("/me")
    fun getProfile() =
        ResponseEntity.ok(getProfileUseCase.execute(SecurityUtils.currentUserEmail()))

    @PutMapping("/me")
    fun updateProfile(@RequestBody @Valid request: UpdateCandidateProfileRequest) =
        ResponseEntity.ok(updateProfileUseCase.execute(SecurityUtils.currentUserEmail(), request))

    //  Work Experience ─
    @GetMapping("/me/experience")
    fun listExperience() =
        ResponseEntity.ok(workExperienceUseCase.list(SecurityUtils.currentUserEmail()))

    @PostMapping("/me/experience")
    fun addExperience(@RequestBody @Valid request: WorkExperienceRequest) =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(workExperienceUseCase.add(SecurityUtils.currentUserEmail(), request))

    @PutMapping("/me/experience/{id}")
    fun updateExperience(
        @PathVariable id: Long,
        @RequestBody @Valid request: WorkExperienceRequest
    ) = ResponseEntity.ok(workExperienceUseCase.update(SecurityUtils.currentUserEmail(), id, request))

    @DeleteMapping("/me/experience/{id}")
    fun deleteExperience(@PathVariable id: Long): ResponseEntity<Void> {
        workExperienceUseCase.delete(SecurityUtils.currentUserEmail(), id)
        return ResponseEntity.noContent().build()
    }

    //  Education ─
    @GetMapping("/me/education")
    fun listEducation() =
        ResponseEntity.ok(educationUseCase.list(SecurityUtils.currentUserEmail()))

    @PostMapping("/me/education")
    fun addEducation(@RequestBody @Valid request: EducationRequest) =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(educationUseCase.add(SecurityUtils.currentUserEmail(), request))

    @PutMapping("/me/education/{id}")
    fun updateEducation(
        @PathVariable id: Long,
        @RequestBody @Valid request: EducationRequest
    ) = ResponseEntity.ok(educationUseCase.update(SecurityUtils.currentUserEmail(), id, request))

    @DeleteMapping("/me/education/{id}")
    fun deleteEducation(@PathVariable id: Long): ResponseEntity<Void> {
        educationUseCase.delete(SecurityUtils.currentUserEmail(), id)
        return ResponseEntity.noContent().build()
    }

    //  Skills 
    @GetMapping("/me/skills")
    fun listSkills() =
        ResponseEntity.ok(skillUseCase.list(SecurityUtils.currentUserEmail()))

    @PostMapping("/me/skills")
    fun addSkills(@RequestBody @Valid request: SkillsRequest) =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(skillUseCase.addSkills(SecurityUtils.currentUserEmail(), request))

    @DeleteMapping("/me/skills/{skillId}")
    fun removeSkill(@PathVariable skillId: Long): ResponseEntity<Void> {
        skillUseCase.removeSkill(SecurityUtils.currentUserEmail(), skillId)
        return ResponseEntity.noContent().build()
    }

    //  Languages
    @GetMapping("/me/languages")
    fun listLanguages() =
        ResponseEntity.ok(languageUseCase.list(SecurityUtils.currentUserEmail()))

    @PostMapping("/me/languages")
    fun addLanguage(@RequestBody @Valid request: LanguageRequest) =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(languageUseCase.add(SecurityUtils.currentUserEmail(), request))

    @DeleteMapping("/me/languages/{language}")
    fun removeLanguage(@PathVariable language: String): ResponseEntity<Void> {
        languageUseCase.remove(SecurityUtils.currentUserEmail(), language)
        return ResponseEntity.noContent().build()
    }
}