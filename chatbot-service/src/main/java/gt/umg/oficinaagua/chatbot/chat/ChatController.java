package gt.umg.oficinaagua.chatbot.chat;

import gt.umg.oficinaagua.chatbot.api.ChatApi;
import gt.umg.oficinaagua.chatbot.api.model.ChatRequest;
import gt.umg.oficinaagua.chatbot.api.model.ChatResponse;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ChatController implements ChatApi {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public ResponseEntity<ChatResponse> enviarPregunta(ChatRequest chatRequest) {
        RespuestaChat resultado = chatService.responder(chatRequest.getPregunta());
        ChatResponse respuesta = new ChatResponse()
                .respuesta(resultado.respuesta())
                .fuentes(resultado.fuentes());
        return ResponseEntity.ok(respuesta);
    }
}