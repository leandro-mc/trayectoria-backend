# Decisiones pendientes — TrayectorIA Backend

> Documento de trabajo. Resume el análisis por capas y deja **cada decisión abierta** para tomarla en una etapa posterior. Nada de lo que aparece como "pendiente" se ha aplicado.

## Estado de esta etapa

| Tema | Resultado |
|------|-----------|
| Rama de control | `develop` (ya existía; se sincronizó con `main`). La rama por defecto sigue siendo `main`. |
| Credenciales en código e historial | Revisados el árbol y los 44 commits de todas las ramas y etiquetas con gitleaks y un barrido propio. Había **un secreto JWT de 64 caracteres escrito en `application-test.yml`** (configuración de pruebas). Se reemplazó en todo el historial por un valor de prueba obviamente falso y se reescribió el historial del repositorio (ver más abajo). Los perfiles de desarrollo y producción ya usan `${JWT_SECRET}`. |
| Ramas existentes | `develop`, nueve ramas `feature/*` ya fusionadas, `main` y siete etiquetas (`v0.2.0` a `v0.7.1`). |

### Sobre la reescritura del historial
- Se conservaron los 44 commits con el mismo autor, correo, fecha y mensaje; cambió solo el contenido de una línea de `application-test.yml` y, por tanto, los identificadores (hashes) de los commits afectados.
- Si en algún momento se usó ese mismo valor como `JWT_SECRET` real, hay que **rotarlo**: borrarlo del historial no lo invalida.
- Quien tenga una copia local antigua debe volver a clonar.

## Resumen por capas

| Capa | Estado actual | Observación principal |
|------|---------------|-----------------------|
| Base de datos | PostgreSQL 16 (compose, puerto 5434, con `healthcheck`); Flyway con `V1__init` (181 líneas) y `V2__seed_roles_and_skills` (62 líneas) | El modo de esquema depende de variables (`JPA_DDL_AUTO`, `FLYWAY_ENABLED`); es posible arrancar sin Flyway por error. |
| Backend | Kotlin 1.9, Spring Boot 3.5, Spring Security con JWT, Spring AI (OpenAI), 10 controllers y 28 casos de uso | 5 pruebas de integración con Testcontainers; sin pruebas de los casos de uso de IA. |
| Frontend | Repositorio aparte: [trayectoria-web](https://github.com/leandro-mc/trayectoria-web) | Consume la API REST; la documentación OpenAPI (Swagger) ya existe. |
| Infraestructura | Dockerfile multi-etapa con usuario no root; `railway.toml` con comprobación de salud; compose solo con la base de datos | Sin integración continua. |

## Decisiones

### D-01 · Base de datos: un único modo de esquema
- **Contexto:** `JPA_DDL_AUTO` y `FLYWAY_ENABLED` se leen de variables de entorno; un comentario del `application.yml` describe cómo alternar entre `create-drop` y `validate`.
- **Opciones:** (A) fijar Flyway activo y `ddl-auto: validate` en todos los perfiles, y usar `create-drop` solo en pruebas; (B) mantener la configuración por variables.
- **Recomendación:** A. Con las migraciones ya escritas, el modo con `create-drop` solo introduce riesgo de configuración.
- **Esfuerzo:** bajo. **Decisión:** ☐ pendiente

### D-02 · Base de datos: datos de demostración
- **Opciones:** (A) perfil `demo` que cargue empresas, ofertas y un candidato de ejemplo además de las semillas de roles y habilidades; (B) dejarlo vacío.
- **Recomendación:** A, para que quien arranque el proyecto vea ofertas y una entrevista simulada de inmediato.
- **Esfuerzo:** bajo. **Decisión:** ☐ pendiente

### D-03 · Backend: arquitectura
- **Contexto:** el README habla de arquitectura limpia, pero 26 de los 28 casos de uso importan entidades y repositorios JPA y 27 importan DTOs de `web`. En la práctica es una arquitectura en capas.
- **Opciones:** (A) aceptarla como arquitectura en capas y corregir la documentación; (B) introducir modelos de dominio y puertos de repositorio para todo; (C) hacerlo solo en el módulo de IA, que ya tiene un puerto (`AiGenerationPort`).
- **Recomendación:** C si se quiere mostrar el patrón sin una reescritura grande; A si se prioriza terminar.
- **Esfuerzo:** A bajo, C medio, B alto. **Decisión:** ☐ pendiente

### D-04 · Backend: pruebas
- **Opciones:** (A) pruebas unitarias de los casos de uso de IA con el puerto simulado y pruebas del analizador del JSON del modelo; (B) pruebas de los prompts con casos de referencia; (C) medir cobertura.
- **Recomendación:** A y C; B después, porque es lo más difícil de verificar.
- **Esfuerzo:** medio. **Decisión:** ☐ pendiente

### D-05 · Backend: robustez de la integración con IA
- **Contexto:** la salida de un modelo no es confiable (formato, latencia, costo).
- **Opciones:** definir tiempos de espera y reintentos, validar el JSON devuelto, limitar el uso por usuario y proteger los prompts frente a inyección desde datos de perfil u ofertas.
- **Recomendación:** hacerlo en una sola rama junto con D-04.
- **Esfuerzo:** medio. **Decisión:** ☐ pendiente

### D-06 · Backend: funcionalidades a medio construir
- **Contexto:** `VoiceInterviewController` es un marcador que siempre responde `available: false`. Las variables de Cloudinary aparecen en `.env.example` y en la configuración de pruebas, pero ningún archivo de `src/main` ni el `build.gradle.kts` las usan.
- **Opciones:** (A) eliminar ambas cosas hasta que existan; (B) terminarlas; (C) dejarlas documentadas como hoja de ruta.
- **Recomendación:** A para la configuración de Cloudinary; C para la entrevista por voz (es una línea de producto declarada).
- **Esfuerzo:** bajo. **Decisión:** ☐ pendiente

### D-07 · Backend: seguridad de la API
- **Contexto:** el actuador solo expone `health` sin detalles (correcto). La configuración de CORS está en `SecurityConfig` con un comentario de que debe cambiarse en producción. No se comprobó que exista límite de intentos en login y refresh.
- **Opciones:** orígenes CORS desde variable de entorno, límite de intentos en autenticación y rotación del token de refresco.
- **Recomendación:** CORS por variable y límite de intentos ahora; la rotación del refresh como segunda fase.
- **Esfuerzo:** medio. **Decisión:** ☐ pendiente

### D-08 · Infraestructura: contenedor completo
- **Contexto:** el Dockerfile ya existe (compilación y ejecución separadas, usuario sin privilegios); el compose solo levanta la base de datos.
- **Opciones:** (A) añadir el servicio `api` al compose, con espera al `healthcheck` y todas las variables en `.env`; (B) dejarlo como está porque el despliegue en Railway usa el Dockerfile directamente.
- **Recomendación:** A, sin tocar el Dockerfile.
- **Esfuerzo:** bajo. **Decisión:** ☐ pendiente

### D-09 · Infraestructura: ¿arranque conjunto con el frontend? (unificar o no)
Esta decisión **no es obligatoria** y es la misma que aparece en el repositorio `trayectoria-web`; se decide una vez para ambos.

| Criterio | Evaluación |
|----------|------------|
| Acoplamiento | Medio: un contrato REST y OpenAPI; sin código compartido. |
| Despliegue | Distinto: el backend se despliega desde su Dockerfile (`railway.toml` en la raíz); el frontend es una aplicación Next.js. |
| Herramientas | Gradle y Node, ciclos de vida distintos. |
| Quién lo clona | Quiere ver la aplicación funcionando con un comando. |

- **Opciones:** (A) monorepo con historial de ambos (obliga a reconfigurar el despliegue en Railway); (B) repos separados, con un compose de desarrollo en el backend que construye el frontend desde su URL de Git; (C) repos separados, cada uno con su arranque.
- **Recomendación:** B para el arranque local y **mantener los repos separados** por el despliegue independiente. A no compensa el coste de reconfigurar despliegues.
- **Esfuerzo:** B bajo. **Decisión:** ☐ pendiente

### D-10 · Integración continua y publicación
- **Opciones:** (A) flujo de GitHub Actions que compile, ejecute las pruebas (Testcontainers necesita Docker, disponible en los ejecutores de GitHub) y construya la imagen; (B) además, publicar la imagen en el registro de contenedores de GitHub.
- **Recomendación:** A; B si se quiere un `docker pull` directo.
- **Esfuerzo:** bajo. **Decisión:** ☐ pendiente

### D-11 · Ramas y etiquetas
- **Contexto:** las nueve ramas `feature/*` (`ai-extras`, `ai-module`, `applications`, `auth`, `company-stats`, `docs-and-deploy`, `flyway-migrations`, `integration-tests`, `profiles-and-offers`) no contienen commits que `main` no tenga.
- **Recomendación:** borrarlas y conservar `main`, `develop` y las etiquetas.
- **Esfuerzo:** bajo. **Decisión:** ☐ pendiente

## Ramas de trabajo propuestas

1. `chore/database-and-security`: D-01, D-02, D-06, D-07.
2. `feat/ai-quality`: D-03 (si se elige C), D-04, D-05.
3. `feat/containers-and-ci`: D-08, D-09, D-10, D-11.

## Orden sugerido

D-01 → D-06 → D-07 → D-04 → D-05 → D-08 → D-10 → D-11 → D-03 → D-09 → D-02.
