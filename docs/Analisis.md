# Análisis técnico — TrayectorIA Backend

Documento de apoyo para futuras mejoras. No forma parte de la documentación principal.

## Qué es y cómo está construido

API Kotlin + Spring Boot 3.5 de ~5.000 líneas: 10 controllers, 28 casos de uso (uno por clase), ~30 clases de persistencia y 26 DTOs. Esquema versionado con Flyway (`V1__init`, `V2__seed`). Integración con OpenAI a través de Spring AI detrás de un puerto (`AiGenerationPort`).

## Arquitectura

El README declara Clean Architecture, y la intención se nota (carpetas `application`, `infrastructure`, `web`, puerto de IA), pero **la regla de dependencia no se cumple por completo**:

- 26 de los 28 casos de uso importan clases de `infrastructure.persistence` (entidades y repositorios JPA).
- 27 importan clases de `web` (DTOs).

En la práctica es una arquitectura en capas pragmática: casos de uso que usan JPA directamente. Es una decisión válida, pero conviene documentarla como tal o corregirla.

## Oportunidades principales

1. **Decidir el nivel de pureza de la arquitectura.** Opción A: aceptar el enfoque en capas y actualizar la documentación. Opción B: introducir modelos de dominio y puertos de repositorio, y que los casos de uso no conozcan DTOs ni entidades.
2. **Pruebas unitarias.** Hay 5 tests de integración con Testcontainers (auth, perfil, ofertas, guardados, seguridad), una base buena. Faltan pruebas de los casos de uso con IA (con el puerto mockeado) y de los prompts, que son la parte más difícil de verificar.
3. **Robustez de la integración con IA.** Revisar timeouts, reintentos, límites de costo/uso por usuario y validación del JSON devuelto por `generateJson`, ya que la salida del modelo no es confiable.
4. **Funcionalidades a medio construir.** `VoiceInterviewController` es un marcador (siempre responde `available: false`) y el almacenamiento de archivos (Cloudinary) aparece en variables de entorno y comentarios sin estar activo. Eliminar o terminar para no confundir.

## Prácticas a revisar

- **Prompts en código** (`CurriculumPrompts`, `InterviewPrompts`): evaluar versionarlos y probarlos con casos de referencia; considerar protección ante *prompt injection* desde datos de perfil/ofertas.
- **Bloques de README comentados** (`[//]: #`) con el backlog de Fase 2: mover a issues o a un `ROADMAP`.
- **Seguridad:** confirmar rate limiting en login/refresh y que `JWT_SECRET` se exija sin valor por defecto (el `application.yml` ya lo toma de entorno, bien).
- Falta CI (compilar + tests en cada push); hoy solo hay configuración de despliegue.
