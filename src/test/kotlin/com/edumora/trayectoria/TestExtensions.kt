package com.edumora.trayectoria

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.test.web.servlet.MvcResult

/** Extrae un campo String del JSON body de un MvcResult */
fun MvcResult.extractField(objectMapper: ObjectMapper, field: String): String =
    objectMapper.readTree(response.contentAsString)[field].asText()

/** Extrae un campo Long del JSON body de un MvcResult */
fun MvcResult.extractLong(objectMapper: ObjectMapper, field: String): Long =
    objectMapper.readTree(response.contentAsString)[field].asLong()

/** Serializa cualquier objeto a JSON String */
fun Any.toJson(objectMapper: ObjectMapper): String =
    objectMapper.writeValueAsString(this)