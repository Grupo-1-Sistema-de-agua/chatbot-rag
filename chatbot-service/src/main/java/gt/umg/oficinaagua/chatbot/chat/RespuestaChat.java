package gt.umg.oficinaagua.chatbot.chat;

import java.util.List;

public record RespuestaChat(
        String respuesta,
        List<String> fuentes,
        boolean conInformacion) {
}
