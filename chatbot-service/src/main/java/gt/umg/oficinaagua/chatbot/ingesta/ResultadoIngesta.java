package gt.umg.oficinaagua.chatbot.ingesta;

import java.time.OffsetDateTime;

public record ResultadoIngesta(
        int documentosMarkdown,
        int documentosReplica,
        int fragmentos,
        boolean replicaDisponible,
        OffsetDateTime fecha) {
}