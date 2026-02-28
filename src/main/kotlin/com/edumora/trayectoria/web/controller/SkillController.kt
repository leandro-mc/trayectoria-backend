package com.edumora.trayectoria.web.controller

import com.edumora.trayectoria.application.usecase.SkillCatalogUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/skills")
class SkillController(private val skillCatalogUseCase: SkillCatalogUseCase) {

    @GetMapping
    fun list(@RequestParam(required = false) search: String?) =
        ResponseEntity.ok(
            if (search.isNullOrBlank()) skillCatalogUseCase.listAll()
            else skillCatalogUseCase.search(search)
        )
}
