package com.edumora.trayectoria.infrastructure.persistence.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Structure of the `content` JSONB field in GeneratedCurriculumEntity.
 * Jackson serializes/deserializes this class to/from the PostgreSQL JSONB.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) -> if the AI adds a field
 * that is not in the class, Jackson ignores it instead of throwing an exception.
 * This makes parsing robust against minor model variations.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CurriculumContent(
    val summary: String = "",
    val experience: List<CurriculumExperience> = emptyList(),
    val education: List<CurriculumEducation> = emptyList(),
    val skills: List<String> = emptyList(),
    val languages: List<CurriculumLanguageItem> = emptyList(),
    val highlights: List<String> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CurriculumExperience(
    val company: String = "",
    val position: String = "",
    val description: String = "",
    val period: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CurriculumEducation(
    val institution: String = "",
    val degree: String = "",
    val period: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CurriculumLanguageItem(
    val language: String = "",
    val level: String = ""
)
