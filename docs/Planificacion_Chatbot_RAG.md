# Planificación del proyecto: Chatbot con RAG

**Feature 2 · Fase 2 · Sistema de Gestión para la Oficina del Agua**
Curso de Desarrollo Web · Universidad Mariano Gálvez de Guatemala, Campus Jutiapa
Autora: Mijeli Azucena Lucero Burgos

---

## Fase 1: Jira

- 1.1. Revisar la estructura del tablero Scrum del grupo (épicas, historias, sprint activo)
- 1.2. Crear la épica o historia general de la Feature 2: Chatbot con RAG
- 1.3. Crear la historia de la fase 0, familiarización del stack, con el enlace al repositorio de práctica
- 1.4. Crear una historia por cada fase de desarrollo (fases 2 a 15)
- 1.5. Agregar las tareas de cada historia, según las subfases de este plan
- 1.6. Asignarte las historias y ubicarlas en el sprint

**Tiempo:** unos 45 minutos

## Fase 2: Repositorio en la organización

- 2.1. Definir el nombre y la descripción del repositorio
- 2.2. Crear el repositorio en la organización de GitHub y definir su visibilidad
- 2.3. Definir la estrategia de ramas (`main`, `developer`, `feature/...` por fase) y la convención de commits
- 2.4. Clonarlo en tu Mac y hacer el primer commit

**Tiempo:** unos 20 minutos

## Fase 3: Proyecto y estructura base

- 3.1. Decidir los IDE: IntelliJ IDEA 2026 para Spring Boot, y para Next.js IntelliJ Ultimate o VS Code
- 3.2. Crear las carpetas del repositorio: `chatbot-service/`, `web/`, `replica/`, `docs/`
- 3.3. Crear el proyecto Spring Boot 4.1 con JDK 17 desde Initializr, con todas sus dependencias
- 3.4. Crear la app Next.js con JavaScript
- 3.5. Crear el `.gitignore`, el `.env.example` y un README inicial
- 3.6. Verificar: `./mvnw compile` y `npm run dev` funcionan. Commit

**Tiempo:** 1 a 1.5 horas

## Fase 4: Infraestructura de datos en Docker Compose

- 4.1. Crear el `docker-compose.yml` con el servicio `pgvector` y su healthcheck
- 4.2. Agregar el servicio `replica`: el volcado `01-agua_db.sql` y el script del usuario `chatbot_ro`
- 4.3. Agregar el servicio `dynamodb` con persistencia en disco (volumen)
- 4.4. Ajustar los puertos para no chocar con la fase 1 (DynamoDB en el 8001, réplica en el 3307)
- 4.5. Verificar: los tres servicios sanos, los permisos de `chatbot_ro` y que una tabla de DynamoDB sobreviva a un reinicio. Commit

**Tiempo:** 1 a 1.5 horas

## Fase 5: Contrato OpenAPI y API base

- 5.1. Escribir `chatbot-service.yaml` con `POST /api/chat` y `POST /api/ingesta` (con token)
- 5.2. Configurar el plugin generador (`useSpringBoot4`, `useJakartaEe`, `generateSupportingFiles`) y generar
- 5.3. Crear los controladores que implementan las interfaces, con respuestas fijas de prueba
- 5.4. Crear el manejador global de errores (400 y 503)
- 5.5. Configurar Swagger UI
- 5.6. Verificar en Swagger los endpoints y las validaciones. Commit

**Tiempo:** 1.5 a 2 horas

## Fase 6: Base de conocimiento

- 6.1. Reunir la información real de la oficina: horarios, métodos de pago, reporte de fugas, trámites
- 6.2. Redactar `calculo-consumo.md`
- 6.3. Redactar `como-pagar.md`
- 6.4. Redactar `fugas.md`
- 6.5. Redactar `oficina.md`
- 6.6. Revisar: secciones separadas por `---`, palabras que usaría un vecino, sin montos y sin marcadores `[CONFIRMAR]`. Commit

**Tiempo:** 1 a 1.5 horas

## Fase 7: Lectura de la réplica

- 7.1. Configurar la segunda conexión (a la réplica) sin afectar la conexión principal a pgvector
- 7.2. Crear el `@Repository` con la consulta de tarifas vigentes (`Tb_Tarifas`, `Tb_Tipos_Servicios`, `anulada`)
- 7.3. Convertir cada tarifa en una oración y en un `Document` con metadatos
- 7.4. Verificar en un log o en un endpoint temporal que salen las tarifas vigentes. Commit

**Tiempo:** 1.5 a 2 horas

## Fase 8: Flujo A, ingesta

- 8.1. Leer los `.md` con `MarkdownDocumentReader`
- 8.2. Unir los documentos de los `.md` con los de las tarifas
- 8.3. Aplicar `TokenTextSplitter`
- 8.4. Vaciar la tabla y ejecutar `vectorStore.add`
- 8.5. Ejecutar la ingesta al arrancar y conectarla a `POST /api/ingesta`
- 8.6. Manejar el caso de la réplica caída: indexar solo los `.md`
- 8.7. Verificar: fragmentos de las dos fuentes en `vector_store` y sin duplicados al reindexar. Commit

**Tiempo:** 1.5 a 2 horas

## Fase 9: Flujo B, chat RAG

- 9.1. Crear `ChatService` con la búsqueda top-k y el umbral
- 9.2. Armar el prompt: instrucciones en `system`, fragmentos y pregunta en `user`
- 9.3. Llamar a `llama3.1:8b` y devolver la respuesta con sus fuentes
- 9.4. Responder sin llamar al LLM cuando no hay fragmentos
- 9.5. Agregar logs de los puntajes de similitud
- 9.6. Conectar `ChatService` a `POST /api/chat`
- 9.7. Verificar con preguntas de tarifas, cálculo, fugas y fuera de tema. Commit

**Tiempo:** 1.5 a 2 horas

## Fase 10: Bitácora en DynamoDB

- 10.1. Crear el bean singleton `DynamoDbClient` con `@Configuration` + `@Bean`
- 10.2. Crear la tabla al arrancar si no existe
- 10.3. Crear el componente que registra pregunta, respuesta, fuentes y fecha
- 10.4. Llamarlo desde `ChatService`, sin que un fallo de la bitácora rompa el chat
- 10.5. Verificar con un `scan` y comprobar que los registros sobreviven a un reinicio. Commit

**Tiempo:** 1 a 1.5 horas

## Fase 11: Revisión de lineamientos del backend

- 11.1. Ningún `@Autowired`: toda la inyección por constructor
- 11.2. `@Scope(SCOPE_SINGLETON)` explícito en los servicios y beans sin estado
- 11.3. Manejo de errores cuando Ollama, pgvector o la réplica no están disponibles
- 11.4. Confirmar que el monolito y su base original no se tocaron
- 11.5. Limpiar código temporal y logs de prueba. Commit

**Tiempo:** unos 45 minutos

## Fase 12: Frontend con Next.js

- 12.1. Crear la página pública
- 12.2. Crear el widget de chat (Client Component)
- 12.3. Crear el route handler `/api/chat` con validación y manejo de errores
- 12.4. Agregar el límite de preguntas por IP
- 12.5. Configurar la variable de entorno del microservicio, sin `NEXT_PUBLIC_`
- 12.6. Verificar en el navegador, y en la pestaña Network que solo aparece `/api/chat`. Commit

**Tiempo:** 2 a 2.5 horas

## Fase 13: Dockerización completa

- 13.1. Crear el Dockerfile de `chatbot-service` (dos etapas, JDK 17)
- 13.2. Crear el Dockerfile de `web`
- 13.3. Agregar `chatbot-service` y `web` al compose, con variables de entorno, `host.docker.internal` para Ollama y `depends_on` con `service_healthy`
- 13.4. Completar el `.env` y el `.env.example`
- 13.5. Verificar desde un clon limpio que `docker compose up -d --build` levanta todo y el chat funciona. Commit

**Tiempo:** 2 a 2.5 horas

## Fase 14: Pruebas y calibración

- 14.1. Ejecutar la batería de preguntas: directas, con otras palabras, cálculos, fugas, fuera de tema, datos personales e inyección
- 14.2. Calibrar el umbral y el top-k con los puntajes reales
- 14.3. Probar la actualización: cambiar una tarifa en la réplica, reindexar y volver a preguntar
- 14.4. Probar fallos: Ollama apagado y réplica apagada
- 14.5. Comprobar el checklist de "terminado" de la 0.1.5. Commit de los ajustes

**Tiempo:** 1.5 a 2 horas

## Fase 15 (final): Documentación y demo

- 15.1. Revisar los lineamientos de documentación del ingeniero
- 15.2. Escribir el README completo
- 15.3. Hacer el diagrama de arquitectura adaptado en Lucidchart
- 15.4. Completar los demás entregables que pidió el ingeniero
- 15.5. Preparar el guion de la demo, con la respuesta inventada de la 0.3 como contraste
- 15.6. Ensayar la demo y hacer la entrega final

**Tiempo:** 2 a 3 horas, según los lineamientos

---

## Resumen de tiempos

| Fase | Contenido | Tiempo aprox. |
|---|---|---|
| 1 | Jira | 0.75 h |
| 2 | Repositorio | 0.3 h |
| 3 | Proyecto y estructura base | 1–1.5 h |
| 4 | Infraestructura de datos | 1–1.5 h |
| 5 | Contrato OpenAPI y API base | 1.5–2 h |
| 6 | Base de conocimiento | 1–1.5 h |
| 7 | Lectura de la réplica | 1.5–2 h |
| 8 | Flujo A, ingesta | 1.5–2 h |
| 9 | Flujo B, chat RAG | 1.5–2 h |
| 10 | Bitácora DynamoDB | 1–1.5 h |
| 11 | Revisión de lineamientos | 0.75 h |
| 12 | Frontend Next.js | 2–2.5 h |
| 13 | Dockerización completa | 2–2.5 h |
| 14 | Pruebas y calibración | 1.5–2 h |
| 15 | Documentación y demo | 2–3 h |
| **Total** | | **unas 20 a 26 horas** |
