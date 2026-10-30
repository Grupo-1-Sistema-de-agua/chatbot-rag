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

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IngestaController implements IngestaApi {

    private final byte[] tokenAdmin;
    private final IngestaService ingestaService;

    public IngestaController(@Value("${chatbot.admin-token:}") String tokenAdmin,
                             IngestaService ingestaService) {
        this.tokenAdmin = tokenAdmin.getBytes(StandardCharsets.UTF_8);
        this.ingestaService = ingestaService;
    }

    @Override
    public ResponseEntity<IngestaResponse> reindexar(String xAdminToken) {
        validarToken(xAdminToken);
        ResultadoIngesta resultado = ingestaService.reindexar();
        IngestaResponse respuesta = new IngestaResponse()
                .documentosMarkdown(resultado.documentosMarkdown())
                .documentosReplica(resultado.documentosReplica())
                .fragmentos(resultado.fragmentos())
                .replicaDisponible(resultado.replicaDisponible())
                .fecha(resultado.fecha());
        return ResponseEntity.ok(respuesta);
    }

    private void validarToken(String tokenRecibido) {
        if (tokenAdmin.length == 0 || tokenRecibido == null
                || !MessageDigest.isEqual(tokenAdmin, tokenRecibido.getBytes(StandardCharsets.UTF_8))) {
            throw new TokenInvalidoException();
        }
    }
}