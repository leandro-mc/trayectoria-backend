package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "skill")
class SkillEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 100)
    var name: String = "",

    // TECHNICAL, SOFT, TOOL, LANGUAGE
    @Column(length = 40)
    var type: String? = null
)