-- V2 - Catálogo inicial de skills y roles
INSERT INTO skill (name, type) VALUES
   ('Java',        'TECHNICAL'),
   ('Kotlin',      'TECHNICAL'),
   ('Python',      'TECHNICAL'),
   ('JavaScript',  'TECHNICAL'),
   ('TypeScript',  'TECHNICAL'),
   ('C++',         'TECHNICAL'),
   ('SQL',         'TECHNICAL'),
   ('Spring Boot', 'TECHNICAL'),
   ('Node.js',     'TECHNICAL'),
   ('Express.js',  'TECHNICAL'),
   ('React',       'TECHNICAL'),
   ('Next.js',     'TECHNICAL'),
   ('Angular',     'TECHNICAL'),
   ('Vue.js',      'TECHNICAL'),
   ('Android',     'TECHNICAL'),
   ('Jetpack Compose',     'TECHNICAL'),
   ('PostgreSQL',  'TECHNICAL'),
   ('MySQL',       'TECHNICAL'),
   ('Oracle DB',   'TECHNICAL'),
   ('MongoDB',     'TECHNICAL'),
   ('Redis',       'TECHNICAL'),
   ('Clean Architecture',  'TECHNICAL'),
   ('REST APIs',   'TECHNICAL'),
   ('Microservices','TECHNICAL'),
   ('JWT',         'TECHNICAL'),
   ('OpenAI API',  'TECHNICAL'),
   ('Prompt Engineering',  'TECHNICAL'),
   ('JUnit',       'TECHNICAL'),
   ('Jest',        'TECHNICAL'),
   ('Docker',      'TOOL'),
   ('Git',         'TOOL'),
   ('Linux',       'TOOL'),
   ('Azure DevOps','TOOL'),
   ('GitHub Actions','TOOL'),
   ('Postman',     'TOOL'),
   ('Figma',       'TOOL'),
   ('Selenium',    'TOOL'),
   ('Scrum',               'SOFT'),
   ('Trabajo en equipo',   'SOFT'),
   ('Comunicación efectiva','SOFT'),
   ('Resolución de problemas','SOFT'),
   ('Aprendizaje continuo','SOFT');



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