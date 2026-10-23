package gt.umg.oficinaagua.chatbot.chat;

import gt.umg.oficinaagua.chatbot.api.ChatApi;
import gt.umg.oficinaagua.chatbot.api.model.ChatRequest;
import gt.umg.oficinaagua.chatbot.api.model.ChatResponse;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ChatController implements ChatApi {

    @Override
    public ResponseEntity<ChatResponse> enviarPregunta(ChatRequest chatRequest) {
        ChatResponse respuesta = new ChatResponse()
                .respuesta("Respuesta de prueba. Pregunta recibida: " + chatRequest.getPregunta())
                .fuentes(List.of("respuesta-fija"));
        return ResponseEntity.ok(respuesta);
    }

}
