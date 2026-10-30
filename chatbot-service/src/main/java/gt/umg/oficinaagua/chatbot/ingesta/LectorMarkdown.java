package gt.umg.oficinaagua.chatbot.ingesta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LectorMarkdown {

    private static final Logger log = LoggerFactory.getLogger(LectorMarkdown.class);

    private final ResourcePatternResolver resolver;
    private final String ubicacion;

    public LectorMarkdown(ResourceLoader resourceLoader,
                          @Value("${chatbot.conocimiento.ubicacion}") String ubicacion) {
        this.resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.ubicacion = ubicacion;
    }

    public List<Document> leer() {
        Resource[] archivos;
        try {
            archivos = resolver.getResources(ubicacion);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer la base de conocimiento en " + ubicacion, e);
        }
        Arrays.sort(archivos, Comparator.comparing(Resource::getFilename));

        List<Document> documentos = new ArrayList<>();
        for (Resource archivo : archivos) {
            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                    .withHorizontalRuleCreateDocument(true)
                    .withIncludeCodeBlock(false)
                    .withIncludeBlockquote(false)
                    .withAdditionalMetadata("fuente", archivo.getFilename())
                    .withAdditionalMetadata("origen", "markdown")
                    .build();

            int secciones = 0;
            for (Document seccion : new MarkdownDocumentReader(archivo, config).get()) {
                String texto = seccion.getText() == null ? "" : seccion.getText().strip();
                if (texto.isEmpty()) {
                    continue;
                }
                Map<String, Object> metadatos = new HashMap<>(seccion.getMetadata());
                Object titulo = metadatos.get("title");
                String contenido = titulo == null ? texto : titulo + "\n" + texto;
                documentos.add(new Document(contenido, metadatos));
                secciones++;
            }
            log.info("Leído {}: {} secciones", archivo.getFilename(), secciones);
        }
        return documentos;
    }
}
