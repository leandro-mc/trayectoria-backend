package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "privilege")
class PrivilegeEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // e.g. READ_PRIVILEGE, WRITE_PRIVILEGE
    @Column(nullable = false, unique = true, length = 255)
    var name: String = ""
)