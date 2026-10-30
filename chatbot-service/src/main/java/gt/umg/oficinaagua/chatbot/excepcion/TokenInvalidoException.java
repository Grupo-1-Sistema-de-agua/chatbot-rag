package gt.umg.oficinaagua.chatbot.excepcion;

public class TokenInvalidoException extends RuntimeException {

    public TokenInvalidoException() {
        super("Token de administración ausente o inválido.");
    }
}