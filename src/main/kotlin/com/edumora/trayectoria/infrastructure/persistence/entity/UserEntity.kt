package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * @Entity  -> JPA managed class, maps to "users" table
 * @Table   -> explicit table name (avoids reserved word conflicts)
 *
 * Note: No role column here. Role is resolved via user_role -> role (Spring Security standard).
 * The allOpen plugin in build.gradle.kts makes this class open automatically,
 * which JPA requires for lazy loading proxies.
 */
@Entity
@Table(name = "users")
class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Maps to BIGSERIAL in PostgreSQL
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 255)
    var email: String = "",

    @Column(nullable = false, length = 255)
    var password: String = "",

    @Column(nullable = false)
    var enabled: Boolean = true,

    @Column(name = "token_expired", nullable = false)
    var tokenExpired: Boolean = false,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    /**
     * @ManyToMany  -> A user can have multiple roles, a role belongs to multiple users
     * @JoinTable   -> defines the join table "user_role" with its FK columns
     * FetchType.EAGER → roles are always loaded with the user (needed for Spring Security)
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<RoleEntity> = mutableSetOf(),

    /**
     * mappedBy -> CandidateProfileEntity owns the FK (user_id).
     * cascade  -> if user is deleted, profile is deleted too.
     * optional -> not every user has a candidate profile (companies don't).
     */
    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], optional = true)
    var candidateProfile: CandidateProfileEntity? = null,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], optional = true)
    var companyProfile: CompanyProfileEntity? = null
)