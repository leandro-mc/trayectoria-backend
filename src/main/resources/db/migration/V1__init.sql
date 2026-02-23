-- ============================================================
--  TrayectorIA — V1 Initial Schema
--  Managed by Flyway — DO NOT edit manually after first run
-- ============================================================

-- AUTH & USERS
CREATE TABLE users (
    id            BIGSERIAL    PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    token_expired BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE role (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE privilege (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE role_privilege (
    role_id      BIGINT NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    privilege_id BIGINT NOT NULL REFERENCES privilege(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, privilege_id)
);

CREATE TABLE user_role (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- SKILLS
CREATE TABLE skill (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(40)
);

-- CANDIDATE PROFILE
CREATE TABLE candidate_profile (
    user_id           BIGINT       PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    first_name        VARCHAR(100),
    last_name         VARCHAR(100),
    phone             VARCHAR(20),
    location          VARCHAR(255),
    bio               TEXT,
    profile_image_url VARCHAR(500),
    linkedin_url      VARCHAR(255),
    github_url        VARCHAR(255),
    portfolio_url     VARCHAR(255),
    birthdate         DATE
);

CREATE TABLE work_experience (
    id           BIGSERIAL    PRIMARY KEY,
    candidate_id BIGINT       NOT NULL REFERENCES candidate_profile(user_id) ON DELETE CASCADE,
    company      VARCHAR(150),
    position     VARCHAR(150),
    description  TEXT,
    start_date   DATE,
    end_date     DATE,
    is_current   BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE education (
    id             BIGSERIAL PRIMARY KEY,
    candidate_id   BIGINT    NOT NULL REFERENCES candidate_profile(user_id) ON DELETE CASCADE,
    institution    VARCHAR(255),
    degree         VARCHAR(255),
    field_of_study VARCHAR(255),
    start_date     DATE,
    end_date       DATE
);

CREATE TABLE candidate_language (
    candidate_id BIGINT       NOT NULL REFERENCES candidate_profile(user_id) ON DELETE CASCADE,
    language     VARCHAR(100) NOT NULL,
    level        VARCHAR(10),
    PRIMARY KEY (candidate_id, language)
);

CREATE TABLE candidate_skill (
    candidate_id BIGINT NOT NULL REFERENCES candidate_profile(user_id) ON DELETE CASCADE,
    skill_id     BIGINT NOT NULL REFERENCES skill(id) ON DELETE CASCADE,
    PRIMARY KEY (candidate_id, skill_id)
);

-- COMPANY PROFILE
CREATE TABLE company_profile (

    user_id      BIGINT       PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    company_name VARCHAR(255),
    industry     VARCHAR(150),
    about        TEXT,
    website      VARCHAR(255),
    logo_url     VARCHAR(500),
    location     VARCHAR(255)
);

-- JOB OFFERS
CREATE TABLE job_offer (
    id                     BIGSERIAL    PRIMARY KEY,
    company_id             BIGINT       NOT NULL REFERENCES company_profile(user_id) ON DELETE CASCADE,
    title                  VARCHAR(255) NOT NULL,
    description            TEXT,
    responsibilities       TEXT,
    requirements           TEXT,
    benefits               TEXT,
    work_mode              VARCHAR(20),
    job_type               VARCHAR(20),
    status                 VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    location               VARCHAR(255),
    interview_instructions TEXT,
    requires_interview     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at             TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at             TIMESTAMP
);

CREATE TABLE job_offer_skill (
    job_offer_id BIGINT NOT NULL REFERENCES job_offer(id) ON DELETE CASCADE,
    skill_id     BIGINT NOT NULL REFERENCES skill(id) ON DELETE CASCADE,
    PRIMARY KEY (job_offer_id, skill_id)
);

-- APPLICATIONS
CREATE TABLE job_application (
    id           BIGSERIAL   PRIMARY KEY,
    candidate_id BIGINT      NOT NULL REFERENCES candidate_profile(user_id) ON DELETE CASCADE,
    job_offer_id BIGINT      NOT NULL REFERENCES job_offer(id) ON DELETE CASCADE,
    curriculum_id BIGINT,    -- FK added after generated_curriculum is created below
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    applied_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP,
    UNIQUE (candidate_id, job_offer_id)
);

CREATE TABLE saved_offer (
    candidate_id BIGINT    NOT NULL REFERENCES candidate_profile(user_id) ON DELETE CASCADE,
    job_offer_id BIGINT    NOT NULL REFERENCES job_offer(id) ON DELETE CASCADE,
    saved_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (candidate_id, job_offer_id)
);

-- AI: GENERATED CURRICULA
CREATE TABLE generated_curriculum (
    id               BIGSERIAL PRIMARY KEY,
    candidate_id     BIGINT    NOT NULL REFERENCES candidate_profile(user_id) ON DELETE CASCADE,
    job_offer_id     BIGINT    REFERENCES job_offer(id) ON DELETE SET NULL,
    content          JSONB     NOT NULL,
    is_ai_generated  BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Add FK from job_application to generated_curriculum now that the table exists
ALTER TABLE job_application
    ADD CONSTRAINT fk_application_curriculum
        FOREIGN KEY (curriculum_id) REFERENCES generated_curriculum(id) ON DELETE SET NULL;

-- AI: SIMULATED INTERVIEWS
CREATE TABLE simulated_interview (
    id           BIGSERIAL   PRIMARY KEY,
    candidate_id BIGINT      NOT NULL REFERENCES candidate_profile(user_id) ON DELETE CASCADE,
    job_offer_id BIGINT      NOT NULL REFERENCES job_offer(id) ON DELETE CASCADE,
    status       VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP
);

CREATE TABLE interview_message (
    id           BIGSERIAL   PRIMARY KEY,
    interview_id BIGINT      NOT NULL REFERENCES simulated_interview(id) ON DELETE CASCADE,
    role         VARCHAR(20) NOT NULL,
    content      TEXT        NOT NULL,
    sent_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- SEED: Initial roles and privileges
INSERT INTO privilege (name) VALUES
    ('READ_PRIVILEGE'),
    ('WRITE_PRIVILEGE');

INSERT INTO role (name) VALUES
    ('ROLE_CANDIDATE'),
    ('ROLE_COMPANY');

INSERT INTO role_privilege (role_id, privilege_id)
SELECT r.id, p.id FROM role r, privilege p
WHERE r.name = 'ROLE_CANDIDATE' AND p.name IN ('READ_PRIVILEGE', 'WRITE_PRIVILEGE');

INSERT INTO role_privilege (role_id, privilege_id)
SELECT r.id, p.id FROM role r, privilege p
WHERE r.name = 'ROLE_COMPANY' AND p.name IN ('READ_PRIVILEGE', 'WRITE_PRIVILEGE');