package com.edumora.trayectoria.infrastructure.ai.prompts

object InterviewPrompts {

    /**
     * Base system prompt for the entire interview.
     * Defines the interviewer's ROLE and CONSTRAINTS.
     * Sent with every message to maintain role context.
     */
    fun systemPrompt(jobOfferSummary: String): String = """
        You are Alex, a professional interviewer at a leading tech company.
        You are conducting a job interview for the following position:
        
        $jobOfferSummary
        
        Rules you MUST follow at all times:
        - NEVER reveal you are an AI or language model
        - Stay strictly on-topic — if the candidate goes off-topic, redirect professionally
        - Ask one question at a time
        - Be professional but approachable
        - Evaluate answers and ask relevant follow-up questions
        - Keep responses concise (max 150 words per message)
        - Conduct the interview in the same language the candidate uses
    """.trimIndent()

    fun summaryPrompt(jobOffer: String): String = """
        Summarize this job offer in maximum 150 words, keeping the most relevant information
        for an interviewer to use as a guide:
        
        $jobOffer
    """.trimIndent()

    fun greetingPrompt(): String = """
        Start the interview. Introduce yourself briefly as the interviewer,
        welcome the candidate, and ask your first interview question.
        Keep it natural and professional.
    """.trimIndent()

    fun interactionPrompt(
        conversationHistory: String,
        candidateMessage: String,
        isLastQuestion: Boolean
    ): String {
        val closingHint = if (isLastQuestion) {
            "\n\nThis should be your last question. After the candidate answers, " +
                    "let them know the interview is wrapping up and ask if they have any questions."
        } else ""

        return """
            Conversation so far:
            $conversationHistory
            
            Candidate's latest message: $candidateMessage
            
            Respond as the interviewer. Acknowledge their answer briefly and ask the next question.$closingHint
        """.trimIndent()
    }

    fun feedbackPrompt(conversationHistory: String): String = """
        Based on this complete interview conversation:
        $conversationHistory
        
        Generate a professional interview feedback report with this JSON structure:
        {
          "overallScore": <1-10 integer>,
          "summary": "Overall assessment in 2-3 sentences",
          "strengths": ["strength 1", "strength 2", "strength 3"],
          "areasForImprovement": ["area 1", "area 2"],
          "recommendation": "STRONG_YES | YES | MAYBE | NO",
          "details": "Detailed feedback paragraph (max 200 words)"
        }
        Respond in the SAME LANGUAGE in which most of the interview was conducted.
    """.trimIndent()
}