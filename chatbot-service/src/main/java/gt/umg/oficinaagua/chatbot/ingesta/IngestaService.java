package gt.umg.oficinaagua.chatbot.ingesta;

import gt.umg.oficinaagua.chatbot.excepcion.ServicioNoDisponibleException;
import gt.umg.oficinaagua.chatbot.replica.ReplicaDocumentosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IngestaService {

    private static final Logger log = LoggerFactory.getLogger(IngestaService.class);

    private final LectorMarkdown lectorMarkdown;
    private final ReplicaDocumentosService replicaDocumentosService;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final TokenTextSplitter divisor;

    public IngestaService(LectorMarkdown lectorMarkdown,
                          ReplicaDocumentosService replicaDocumentosService,
                          VectorStore vectorStore,
                          JdbcTemplate jdbcTemplate) {
        this.lectorMarkdown = lectorMarkdown;
        this.replicaDocumentosService = replicaDocumentosService;
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
        this.divisor = TokenTextSplitter.builder().build();
    }

    @Transactional
    public synchronized ResultadoIngesta reindexar() {
        List<Document> markdown = lectorMarkdown.leer();

        List<Document> replica = new ArrayList<>();
        boolean replicaDisponible = true;
        try {
            replica = replicaDocumentosService.generarDocumentos();
        } catch (DataAccessException e) {
            replicaDisponible = false;
            log.warn("La réplica no está disponible; se indexan solo los documentos .md. Causa: {}", e.getMessage());
        }

        List<Document> todos = new ArrayList<>(markdown);
        todos.addAll(replica);
        List<Document> fragmentos = divisor.apply(todos);

        try {
            jdbcTemplate.execute("TRUNCATE TABLE vector_store");
            vectorStore.add(fragmentos);
        } catch (DataAccessException e) {
            throw new ServicioNoDisponibleException(
                    "No se pudo guardar en la base vectorial. Verifica que pgvector esté disponible.", e);
        } catch (RuntimeException e) {
            throw new ServicioNoDisponibleException(
                    "No se pudieron generar los embeddings. Verifica que Ollama esté disponible.", e);
        }

        log.info("Ingesta completada: {} secciones .md, {} documentos de la réplica, {} fragmentos indexados",
                markdown.size(), replica.size(), fragmentos.size());

        return new ResultadoIngesta(markdown.size(), replica.size(), fragmentos.size(),
                replicaDisponible, OffsetDateTime.now());
    }
}