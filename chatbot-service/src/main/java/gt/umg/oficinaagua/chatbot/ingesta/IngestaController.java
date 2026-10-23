package gt.umg.oficinaagua.chatbot.ingesta;

import gt.umg.oficinaagua.chatbot.api.IngestaApi;
import gt.umg.oficinaagua.chatbot.api.model.IngestaResponse;
import gt.umg.oficinaagua.chatbot.excepcion.TokenInvalidoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IngestaController implements IngestaApi {

    private final byte[] tokenAdmin;

    public IngestaController(@Value("${chatbot.admin-token:}") String tokenAdmin) {
        this.tokenAdmin = tokenAdmin.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public ResponseEntity<IngestaResponse> reindexar(String xAdminToken) {
        validarToken(xAdminToken);
        IngestaResponse respuesta = new IngestaResponse()
                .documentosMarkdown(0)
                .documentosReplica(0)
                .fragmentos(0)
                .replicaDisponible(false)
                .fecha(OffsetDateTime.now());
        return ResponseEntity.ok(respuesta);
    }

    private void validarToken(String tokenRecibido) {
        if (tokenAdmin.length == 0 || tokenRecibido == null
                || !MessageDigest.isEqual(tokenAdmin, tokenRecibido.getBytes(StandardCharsets.UTF_8))) {
            throw new TokenInvalidoException();
        }
    }
}