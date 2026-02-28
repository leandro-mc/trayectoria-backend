package com.edumora.trayectoria.web.dto.response

import java.time.LocalDate

data class CandidateProfileResponse(
    val userId: Long,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val phone: String?,
    val location: String?,
    val bio: String?,
    val profileImageUrl: String?,
    val linkedinUrl: String?,
    val githubUrl: String?,
    val portfolioUrl: String?,
    val birthdate: LocalDate?,
    val skills: List<SkillResponse>,
    val workExperiences: List<WorkExperienceResponse>,
    val educations: List<EducationResponse>,
    val languages: List<LanguageResponse>
)

data class WorkExperienceResponse(
    val id: Long,
    val company: String?,
    val position: String?,
    val description: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val isCurrent: Boolean
)

data class EducationResponse(
    val id: Long,
    val institution: String?,
    val degree: String?,
    val fieldOfStudy: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)

data class LanguageResponse(
    val language: String,
    val level: String?
)

data class SkillResponse(
    val id: Long,
    val name: String,
    val type: String?
)