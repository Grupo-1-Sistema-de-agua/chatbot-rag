package gt.umg.oficinaagua.chatbot.replica;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CatalogoReplicaRepository {

    private static final String SQL_TARIFAS_VIGENTES = """
            SELECT ts.codigo, ts.nombre, ts.volumen_incluido_litros, ts.es_servicio,
                   t.precio, t.vigente_desde
            FROM Tb_Tarifas t
            JOIN Tb_Tipos_Servicios ts ON ts.id = t.tipo_servicio_id
            WHERE t.anulada = 0
              AND t.vigente_desde <= NOW()
              AND (t.vigente_hasta IS NULL OR t.vigente_hasta > NOW())
            ORDER BY ts.es_servicio DESC, t.precio
            """;

    private static final String SQL_SECTORES = "SELECT nombre FROM Tb_Sectores ORDER BY nombre";

    private static final String SQL_METODOS_PAGO = "SELECT nombre FROM Tb_Metodos_Pago ORDER BY nombre";

    private final JdbcClient jdbcClient;

    public CatalogoReplicaRepository(@Qualifier("replicaJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<TarifaVigente> buscarTarifasVigentes() {
        return jdbcClient.sql(SQL_TARIFAS_VIGENTES)
                .query((rs, fila) -> {
                    int volumen = rs.getInt("volumen_incluido_litros");
                    Integer volumenIncluido = rs.wasNull() ? null : volumen;
                    Timestamp vigenteDesde = rs.getTimestamp("vigente_desde");
                    return new TarifaVigente(
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            volumenIncluido,
                            rs.getBoolean("es_servicio"),
                            rs.getBigDecimal("precio"),
                            vigenteDesde == null ? null : vigenteDesde.toLocalDateTime());
                })
                .list();
    }

    public List<String> buscarSectores() {
        return jdbcClient.sql(SQL_SECTORES).query(String.class).list();
    }

    public List<String> buscarMetodosPago() {
        return jdbcClient.sql(SQL_METODOS_PAGO).query(String.class).list();
    }
}