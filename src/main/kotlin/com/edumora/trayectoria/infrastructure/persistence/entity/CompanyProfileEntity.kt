package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "company_profile")
class CompanyProfileEntity(

    @Id
    var userId: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    var user: UserEntity? = null,

    @Column(name = "company_name", length = 255)
    var companyName: String? = null,

    @Column(length = 150)
    var industry: String? = null,

    @Column(columnDefinition = "TEXT")
    var about: String? = null,

    @Column(length = 255)
    var website: String? = null,

    @Column(name = "logo_url", length = 500)
    var logoUrl: String? = null,

    @Column(length = 255)
    var location: String? = null,

    @OneToMany(mappedBy = "company", cascade = [CascadeType.ALL], orphanRemoval = true)
    var jobOffers: MutableList<JobOfferEntity> = mutableListOf()
)