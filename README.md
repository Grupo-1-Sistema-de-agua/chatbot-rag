# Chatbot con RAG · Oficina del Agua

Feature 2 de la Fase 2 del Sistema de Gestión para la Oficina del Agua: un chatbot en la página pública que responde las preguntas de los vecinos sobre tarifas, consumo, pagos y fugas, usando RAG (Retrieval-Augmented Generation) sin modificar el monolito de la fase 1.

**Stack:** Spring Boot 4.1 (JDK 17) · Spring AI 2.0 · PostgreSQL + pgvector · Ollama · DynamoDB Local · Next.js (BFF) · Docker Compose

**Autora:** Mijeli Azucena Lucero Burgos

> Proyecto en desarrollo. La documentación completa se agregará al finalizar.

## Estructura del repositorio

| Carpeta | Contenido |
|---|---|
| `chatbot-service/` | Microservicio Spring Boot 4.1 (JDK 17) con Spring AI: ingesta y chat RAG |
| `web/` | Página pública en Next.js con el widget de chat y el BFF |
| `replica/` | Scripts de inicialización de la réplica de la base de datos |
| `docs/` | Planificación y documentación del proyecto |
