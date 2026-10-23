package gt.umg.oficinaagua.chatbot.excepcion;

import gt.umg.oficinaagua.chatbot.api.model.RespuestaError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ManejadorGlobalErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorGlobalErrores.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return construir(HttpStatus.BAD_REQUEST, "SOLICITUD_INVALIDA", mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaError> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        return construir(HttpStatus.BAD_REQUEST, "SOLICITUD_INVALIDA",
                "El cuerpo de la solicitud no es un JSON válido.");
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<RespuestaError> manejarTokenInvalido(TokenInvalidoException ex) {
        return construir(HttpStatus.UNAUTHORIZED, "NO_AUTORIZADO", ex.getMessage());
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<RespuestaError> manejarServicioNoDisponible(ServicioNoDisponibleException ex) {
        log.error("Servicio no disponible: {}", ex.getMessage(), ex);
        return construir(HttpStatus.SERVICE_UNAVAILABLE, "SERVICIO_NO_DISPONIBLE", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarGeneral(Exception ex) {
        if (ex instanceof ErrorResponse errorSpring) {
            HttpStatus estado = HttpStatus.valueOf(errorSpring.getStatusCode().value());
            return construir(estado, estado.name(), estado.getReasonPhrase());
        }
        log.error("Error no controlado", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO",
                "Ocurrió un error inesperado.");
    }

    private ResponseEntity<RespuestaError> construir(HttpStatus estado, String codigo, String mensaje) {
        RespuestaError cuerpo = new RespuestaError()
                .codigo(codigo)
                .mensaje(mensaje)
                .fecha(OffsetDateTime.now());
        return ResponseEntity.status(estado).body(cuerpo);
    }
}