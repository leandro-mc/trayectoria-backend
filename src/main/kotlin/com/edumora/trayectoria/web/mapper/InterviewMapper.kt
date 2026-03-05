package com.edumora.trayectoria.web.mapper

import com.edumora.trayectoria.infrastructure.persistence.entity.InterviewMessageEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.SimulatedInterviewEntity
import com.edumora.trayectoria.web.dto.response.InterviewMessageResponse
import com.edumora.trayectoria.web.dto.response.SimulatedInterviewResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface InterviewMapper {

    @Mapping(
        target = "jobOfferId",
        expression = "java(entity.getJobOffer() != null ? entity.getJobOffer().getId() : null)"
    )
    @Mapping(
        target = "jobOfferTitle",
        expression = "java(entity.getJobOffer() != null ? entity.getJobOffer().getTitle() : null)"
    )
    @Mapping(target = "messages", expression = "java(java.util.Collections.emptyList())")
    fun toSummaryResponse(entity: SimulatedInterviewEntity): SimulatedInterviewResponse

    @Mapping(
        target = "jobOfferId",
        expression = "java(entity.getJobOffer() != null ? entity.getJobOffer().getId() : null)"
    )
    @Mapping(
        target = "jobOfferTitle",
        expression = "java(entity.getJobOffer() != null ? entity.getJobOffer().getTitle() : null)"
    )
    fun toDetailResponse(entity: SimulatedInterviewEntity): SimulatedInterviewResponse

    fun toMessageResponse(entity: InterviewMessageEntity): InterviewMessageResponse

    @Mapping(
        target = "jobOfferId",
        expression = "java(entity.getJobOffer() != null ? entity.getJobOffer().getId() : null)"
    )
    @Mapping(
        target = "jobOfferTitle",
        expression = "java(entity.getJobOffer() != null ? entity.getJobOffer().getTitle() : null)"
    )
    @Mapping(target = "messages", source = "filteredMessages")
    fun toDetailResponse(
        entity: SimulatedInterviewEntity,
        filteredMessages: List<InterviewMessageEntity>
    ): SimulatedInterviewResponse
}
