# Patrón Singleton en el microservicio chatbot-service

## Qué es

Singleton es un patrón de diseño creacional que garantiza que una clase tenga **una sola instancia** durante toda la ejecución de la aplicación y ofrece un punto de acceso global a ella.

Se usa cuando un objeto:
- es costoso de crear (conexiones, clientes de red, pools),
- debe compartirse entre todos los componentes,
- y no guarda estado propio de cada petición.

## Estructura clásica (sin Spring)

```java
public final class ClienteDynamo {

    private static volatile ClienteDynamo instancia;   // 1. instancia única, estática

    private ClienteDynamo() { }                         // 2. constructor privado

    public static ClienteDynamo getInstance() {         // 3. punto de acceso global
        if (instancia == null) {
            synchronized (ClienteDynamo.class) {
                if (instancia == null) {
                    instancia = new ClienteDynamo();
                }
            }
        }
        return instancia;
    }
}
```

Problemas de esta forma: la clase controla su propia creación, es difícil de probar (no se puede sustituir por un simulado), hay que manejar a mano la concurrencia y la dependencia queda oculta dentro del código que llama a `getInstance()`.

## Singleton en Spring Boot

En Spring, el contenedor de inversión de control (IoC) es quien crea los objetos (beans) y los entrega por **inyección de dependencias**. El alcance (`scope`) por defecto de todo bean es `singleton`: el contenedor crea **una sola instancia por contexto** y entrega esa misma instancia a todos los que la necesitan.

Ventajas frente al Singleton clásico:
- La clase es un objeto normal, con constructor público y sin estado estático.
- La dependencia es explícita en el constructor y se puede sustituir en pruebas.
- Spring garantiza una sola instancia, la crea al arrancar y la cierra al apagar (por ejemplo, llama a `close()` del cliente de DynamoDB y de los pools de conexiones).

## Cómo se configura

1. **Clases propias** con `@Component`, `@Service`, `@Repository` o `@RestController`. En este proyecto el alcance se declara de forma explícita:

```java
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ChatService { ... }
```

2. **Objetos de librerías externas** con `@Configuration` + `@Bean`. Spring ejecuta el método una sola vez y guarda el resultado. La clase `@Configuration` se envuelve en un proxy (CGLIB) para que, aunque el método se llame varias veces, siempre se devuelva la misma instancia:

```java
@Configuration
public class DynamoDbConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DynamoDbClient dynamoDbClient(...) {
        return DynamoDbClient.builder()...build();
    }
}
```

3. **Inyección por constructor**, sin `@Autowired`. Cuando una clase tiene un solo constructor, Spring lo usa automáticamente para inyectar los singletons que necesita:

```java
public BitacoraService(DynamoDbClient dynamoDb, @Value("${chatbot.bitacora.tabla}") String tabla) {
    this.dynamoDb = dynamoDb;
    this.tabla = tabla;
}
```

## Requisito: beans sin estado

Como una sola instancia atiende peticiones simultáneas desde varios hilos, los singletons de este proyecto no guardan datos de cada petición. Solo tienen campos `final` asignados en el constructor (otros beans o configuración). Lo que cambia por petición (la pregunta, los fragmentos, la respuesta) son variables locales de los métodos.

## Singletons del proyecto

| Bean | Tipo | Por qué singleton |
|---|---|---|
| `DynamoDbClient` | `@Bean` en `DynamoDbConfig` | Cliente de red costoso y seguro entre hilos; mantiene conexiones abiertas |
| `pgvectorDataSource`, `replicaDataSource` | `@Bean` en `DataSourceConfig` | Pools de conexiones (Hikari); debe haber uno por base |
| `replicaJdbcClient` | `@Bean` en `DataSourceConfig` | Cliente JDBC de solo lectura sobre el pool de la réplica |
| `ChatService` | `@Service` | Orquesta el flujo RAG; sin estado |
| `IngestaService` | `@Service` | Reindexa la base; `synchronized` evita dos ingestas simultáneas |
| `LectorMarkdown` | `@Component` | Lee la base de conocimiento |
| `CatalogoReplicaRepository` | `@Repository` | Consultas a la réplica |
| `ReplicaDocumentosService` | `@Service` | Convierte catálogos en documentos |
| `BitacoraService` | `@Service` | Registra cada consulta en DynamoDB |
| `ChatController`, `IngestaController` | `@RestController` | Implementan las interfaces generadas desde OpenAPI |
| `ManejadorGlobalErrores` | `@RestControllerAdvice` | Traduce excepciones a respuestas HTTP |
| `IngestaInicial`, `BitacoraTablaInicializador` | `@Component` | Tareas de arranque |

Los beans de Spring AI (`VectorStore`, `ChatModel`, `EmbeddingModel`) también son singletons creados por la autoconfiguración.