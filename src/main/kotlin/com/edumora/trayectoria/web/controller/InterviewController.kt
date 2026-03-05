package com.edumora.trayectoria.web.controller

import com.edumora.trayectoria.application.usecase.ai.CompleteInterviewUseCase
import com.edumora.trayectoria.application.usecase.ai.GetInterviewsUseCase
import com.edumora.trayectoria.application.usecase.ai.SendInterviewMessageUseCase
import com.edumora.trayectoria.application.usecase.ai.StartInterviewUseCase
import com.edumora.trayectoria.shared.util.SecurityUtils
import com.edumora.trayectoria.web.dto.request.ai.SendMessageRequest
import com.edumora.trayectoria.web.dto.request.ai.StartInterviewRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/interviews")
@PreAuthorize("hasRole('CANDIDATE')")
class InterviewController(
    private val startUseCase: StartInterviewUseCase,
    private val sendMessageUseCase: SendInterviewMessageUseCase,
    private val completeUseCase: CompleteInterviewUseCase,
    private val getInterviewsUseCase: GetInterviewsUseCase
) {
    @PostMapping
    fun start(@RequestBody @Valid request: StartInterviewRequest) =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(startUseCase.execute(SecurityUtils.currentUserEmail(), request))

    @GetMapping
    fun listAll() =
        ResponseEntity.ok(getInterviewsUseCase.listAll(SecurityUtils.currentUserEmail()))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) =
        ResponseEntity.ok(getInterviewsUseCase.getById(SecurityUtils.currentUserEmail(), id))

    @PostMapping("/{id}/messages")
    fun sendMessage(
        @PathVariable id: Long,
        @RequestBody @Valid request: SendMessageRequest
    ) = ResponseEntity.status(HttpStatus.CREATED)
        .body(sendMessageUseCase.execute(SecurityUtils.currentUserEmail(), id, request))

    @PatchMapping("/{id}/complete")
    fun complete(@PathVariable id: Long) =
        ResponseEntity.ok(completeUseCase.execute(SecurityUtils.currentUserEmail(), id))
}