package com.edumora.trayectoria.web.controller

import com.edumora.trayectoria.application.usecase.SavedOfferUseCase
import com.edumora.trayectoria.shared.util.SecurityUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/saved-offers")
@PreAuthorize("hasRole('CANDIDATE')")
class SavedOfferController(
    private val savedOfferUseCase: SavedOfferUseCase
) {
    /**
     * GET /v1/saved-offers
     * Lista las ofertas guardadas del candidato autenticado, paginadas.
     * Retorna JobOfferSummaryResponse — misma estructura que el listado de ofertas.
     */
    @GetMapping
    fun listSaved(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ) = ResponseEntity.ok(
        savedOfferUseCase.listSaved(
            SecurityUtils.currentUserEmail(),
            PageRequest.of(page, size, Sort.by("id.jobOfferId").descending())
        )
    )

    /**
     * POST /v1/saved-offers/{jobOfferId}
     * Guarda una oferta. No body necesario — el ID va en la URL.
     * 201 si se guardó, 409 si ya estaba guardada.
     */
    @PostMapping("/{jobOfferId}")
    fun save(@PathVariable jobOfferId: Long): ResponseEntity<Void> {
        savedOfferUseCase.save(SecurityUtils.currentUserEmail(), jobOfferId)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    /**
     * DELETE /v1/saved-offers/{jobOfferId}
     * Elimina una oferta guardada.
     * 204 si se eliminó, 404 si no estaba guardada.
     */
    @DeleteMapping("/{jobOfferId}")
    fun remove(@PathVariable jobOfferId: Long): ResponseEntity<Void> {
        savedOfferUseCase.remove(SecurityUtils.currentUserEmail(), jobOfferId)
        return ResponseEntity.noContent().build()
    }

    /**
     * GET /v1/saved-offers/{jobOfferId}/check
     * Endpoint auxiliar — el frontend lo usa para saber si una oferta
     * ya está guardada y mostrar el ícono correcto (bookmark lleno/vacío).
     * Retorna { "saved": true/false }
     */
    @GetMapping("/{jobOfferId}/check")
    fun isSaved(@PathVariable jobOfferId: Long): ResponseEntity<Map<String, Boolean>> =
        ResponseEntity.ok(
            mapOf("saved" to savedOfferUseCase.isSaved(SecurityUtils.currentUserEmail(), jobOfferId))
        )
}