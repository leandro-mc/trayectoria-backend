package com.edumora.trayectoria.infrastructure.ai.prompts

/**
 * Kotlin object (singleton) — Never a @Service.
 * Prompts are pure functions: same input = same output.
 * No dependency injection required; they are stateless.
 *
 * Decoupled from the use case: when the prompt needs tuning,
 * there is no need to modify the business logic.
 */
object CurriculumPrompts {

    fun systemPrompt(): String = """
        You are an expert HR consultant and career coach with 15+ years of experience
        optimizing resumes to maximize hiring probability.
        
        Your task is to personalize a candidate's resume for a specific job offer.
        You must:
        - Highlight relevant skills and experiences for the role
        - Rewrite descriptions to emphasize what matters for THIS specific job
        - Keep all information truthful — only improve presentation, never invent
        - Maintain the exact same JSON structure as the input
        
        Respond in the SAME LANGUAGE as the job offer and candidate profile.
    """.trimIndent()

    fun userPrompt(jobOfferDetails: String, candidateProfileJson: String): String = """
        Job Offer:
        $jobOfferDetails
        
        Candidate Profile (JSON):
        $candidateProfileJson
        
        Generate a personalized curriculum for this specific job offer.
        
        Rules:
        1. summary: Rewrite to highlight qualities relevant to this role (max 200 words)
        2. experience: Rewrite descriptions emphasizing relevant skills (max 100 words each)
        3. skills: Reorder putting most relevant first. Add max 3 new skills inferred from experience.
        4. education: Include only the most relevant entries
        5. languages: Keep as-is
        6. highlights: Add 3-5 bullet points of why this candidate is a great fit for THIS specific role
        
        Return a JSON object with this exact structure:
        {
          "summary": "string",
          "experience": [{"company": "string", "position": "string", "description": "string", "period": "string"}],
          "education": [{"institution": "string", "degree": "string", "period": "string"}],
          "skills": ["string"],
          "languages": [{"language": "string", "level": "string"}],
          "highlights": ["string"]
        }
    """.trimIndent()
}