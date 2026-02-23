package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate

/**
 * @OneToOne with UserEntity.
 * @MapsId -> shares the same PK as UserEntity (user_id = candidate's PK).
 * This avoids a separate auto-generated ID - the candidate's PK IS the user's PK.
 */
@Entity
@Table(name = "candidate_profile")
class CandidateProfileEntity(

    @Id
    var userId: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    var user: UserEntity? = null,

    @Column(name = "first_name", length = 100)
    var firstName: String? = null,

    @Column(name = "last_name", length = 100)
    var lastName: String? = null,

    @Column(length = 20)
    var phone: String? = null,

    @Column(length = 255)
    var location: String? = null,

    @Column(columnDefinition = "TEXT")
    var bio: String? = null,

    @Column(name = "profile_image_url", length = 500)
    var profileImageUrl: String? = null,

    @Column(name = "linkedin_url", length = 255)
    var linkedinUrl: String? = null,

    @Column(name = "github_url", length = 255)
    var githubUrl: String? = null,

    @Column(name = "portfolio_url", length = 255)
    var portfolioUrl: String? = null,

    var birthdate: LocalDate? = null,

    /**
     * @OneToMany  -> One candidate has many work experiences
     * mappedBy   -> WorkExperienceEntity owns the FK (candidate_id)
     * cascade    -> ALL: persist/merge/delete propagate to children
     * orphanRemoval -> deleting from this list also deletes from DB
     */
    @OneToMany(mappedBy = "candidate", cascade = [CascadeType.ALL], orphanRemoval = true)
    var workExperiences: MutableList<WorkExperienceEntity> = mutableListOf(),

    @OneToMany(mappedBy = "candidate", cascade = [CascadeType.ALL], orphanRemoval = true)
    var educations: MutableList<EducationEntity> = mutableListOf(),

    /**
     * @ElementCollection -> A collection of simple/embeddable types (not a full entity).
     * @CollectionTable   -> specifies the join table name and FK column.
     * Good for candidate_language since it has no identity of its own.
     */
    @ElementCollection
    @CollectionTable(
        name = "candidate_language",
        joinColumns = [JoinColumn(name = "candidate_id")]
    )
    var languages: MutableList<CandidateLanguageEmbeddable> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "candidate_skill",
        joinColumns = [JoinColumn(name = "candidate_id")],
        inverseJoinColumns = [JoinColumn(name = "skill_id")]
    )
    var skills: MutableSet<SkillEntity> = mutableSetOf()
)