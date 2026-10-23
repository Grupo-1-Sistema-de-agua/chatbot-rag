package gt.umg.oficinaagua.chatbot.replica;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TarifaVigente(
        String codigo,
        String nombre,
        Integer volumenIncluidoLitros,
        boolean esServicio,
        BigDecimal precio,
        LocalDateTime vigenteDesde) {
}
