package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "role")
class RoleEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // ROLE_CANDIDATE, ROLE_COMPANY
    // Spring Security expects the ROLE_ prefix convention
    @Column(nullable = false, unique = true, length = 255)
    var name: String = "",

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_privilege",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "privilege_id")]
    )
    var privileges: MutableSet<PrivilegeEntity> = mutableSetOf()
)