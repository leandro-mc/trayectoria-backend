package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.JobOfferEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.SkillEntity
import jakarta.persistence.criteria.Join
import org.springframework.data.jpa.domain.Specification

/**
 * Specification pattern — builds dynamic queries.
 * Each function returns a Specification<JobOfferEntity> that can be
 * combined using .and() and .or().
 *
 * The repository uses JpaSpecificationExecutor to execute them.
 * This avoids having a separate method for every filter combination.
 */
object JobOfferSpecification {

    fun hasStatus(status: String): Specification<JobOfferEntity> =
        Specification { root, _, cb ->
            cb.equal(root.get<String>("status"), status)
        }

    fun hasWorkMode(workMode: String): Specification<JobOfferEntity> =
        Specification { root, _, cb ->
            cb.equal(root.get<String>("workMode"), workMode)
        }

    fun hasJobType(jobType: String): Specification<JobOfferEntity> =
        Specification { root, _, cb ->
            cb.equal(root.get<String>("jobType"), jobType)
        }

    fun hasSkill(skillId: Long): Specification<JobOfferEntity> =
        Specification { root, _, cb ->
            val skillJoin: Join<JobOfferEntity, SkillEntity> = root.join("skills")
            cb.equal(skillJoin.get<Long>("id"), skillId)
        }

    fun titleContains(keyword: String): Specification<JobOfferEntity> =
        Specification { root, _, cb ->
            cb.like(cb.lower(root.get("title")), "%${keyword.lowercase()}%")
        }
}