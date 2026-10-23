package gt.umg.oficinaagua.chatbot.bitacora;

import gt.umg.oficinaagua.chatbot.chat.RespuestaChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BitacoraService {

    private static final Logger log = LoggerFactory.getLogger(BitacoraService.class);

    private final DynamoDbClient dynamoDb;
    private final String tabla;

    public BitacoraService(DynamoDbClient dynamoDb,
                           @Value("${chatbot.bitacora.tabla}") String tabla) {
        this.dynamoDb = dynamoDb;
        this.tabla = tabla;
    }

    public void registrar(String pregunta, RespuestaChat respuesta, long duracionMs) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.fromS(UUID.randomUUID().toString()));
        item.put("fecha", AttributeValue.fromS(OffsetDateTime.now().toString()));
        item.put("pregunta", AttributeValue.fromS(pregunta));
        item.put("respuesta", AttributeValue.fromS(respuesta.respuesta()));
        item.put("fuentes", AttributeValue.fromL(
                respuesta.fuentes().stream().map(AttributeValue::fromS).toList()));
        item.put("conInformacion", AttributeValue.fromBool(respuesta.conInformacion()));
        item.put("duracionMs", AttributeValue.fromN(Long.toString(duracionMs)));

        try {
            dynamoDb.putItem(solicitud -> solicitud.tableName(tabla).item(item));
        } catch (RuntimeException e) {
            log.warn("No se pudo registrar la consulta en la bitácora: {}", e.getMessage());
        }
    }
}
