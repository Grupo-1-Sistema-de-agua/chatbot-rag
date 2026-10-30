package gt.umg.oficinaagua.chatbot.bitacora;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BitacoraTablaInicializador implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BitacoraTablaInicializador.class);

    private final DynamoDbClient dynamoDb;
    private final String tabla;

    public BitacoraTablaInicializador(DynamoDbClient dynamoDb,
                                      @Value("${chatbot.bitacora.tabla}") String tabla) {
        this.dynamoDb = dynamoDb;
        this.tabla = tabla;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (existe()) {
                log.info("Tabla de bitácora {} encontrada", tabla);
                return;
            }
            dynamoDb.createTable(CreateTableRequest.builder()
                    .tableName(tabla)
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName("id")
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("id")
                            .keyType(KeyType.HASH)
                            .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build());
            dynamoDb.waiter().waitUntilTableExists(solicitud -> solicitud.tableName(tabla));
            log.info("Tabla de bitácora {} creada", tabla);
        } catch (RuntimeException e) {
            log.warn("No se pudo verificar o crear la tabla de bitácora {}: {}", tabla, e.getMessage());
        }
    }

    private boolean existe() {
        try {
            dynamoDb.describeTable(solicitud -> solicitud.tableName(tabla));
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
