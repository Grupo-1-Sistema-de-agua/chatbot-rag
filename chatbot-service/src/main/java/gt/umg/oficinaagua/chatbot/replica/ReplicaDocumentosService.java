package gt.umg.oficinaagua.chatbot.replica;

import org.springframework.ai.document.Document;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ReplicaDocumentosService {

    public static final String FUENTE_TARIFAS = "Tarifas vigentes";
    public static final String FUENTE_SECTORES = "Sectores";
    public static final String FUENTE_METODOS_PAGO = "Métodos de pago";

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CatalogoReplicaRepository repositorio;

    public ReplicaDocumentosService(CatalogoReplicaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Document> generarDocumentos() {
        List<Document> documentos = new ArrayList<>();
        documentos.addAll(documentosDeTarifas(repositorio.buscarTarifasVigentes()));
        documentos.add(documentoDeSectores(repositorio.buscarSectores()));
        documentos.add(documentoDeMetodosPago(repositorio.buscarMetodosPago()));
        return documentos;
    }

    private List<Document> documentosDeTarifas(List<TarifaVigente> tarifas) {
        TarifaVigente exceso = tarifas.stream()
                .filter(tarifa -> !tarifa.esServicio())
                .findFirst()
                .orElse(null);

        List<Document> documentos = new ArrayList<>();
        List<String> resumen = new ArrayList<>();

        for (TarifaVigente tarifa : tarifas) {
            String texto;
            if (tarifa.esServicio()) {
                String reglaExceso = exceso == null ? "" : String.format(
                        " Si el consumo del mes pasa de %s litros, se suma un exceso de %s por cada bloque de 1,000 litros o fracción de más.",
                        litros(tarifa.volumenIncluidoLitros()), quetzales(exceso.precio()));
                texto = String.format(
                        "Tarifa vigente del servicio de agua %s (código %s): la cuota mensual es de %s e incluye %s litros de agua al mes.%s Vigente desde el %s.",
                        tarifa.nombre(), tarifa.codigo(), quetzales(tarifa.precio()),
                        litros(tarifa.volumenIncluidoLitros()), reglaExceso, fecha(tarifa));
                resumen.add(String.format("%s: %s al mes con %s litros incluidos",
                        tarifa.nombre(), quetzales(tarifa.precio()), litros(tarifa.volumenIncluidoLitros())));
            } else {
                texto = String.format(
                        "Precio vigente del exceso de consumo (%s), es decir, cuánto se paga por los litros de exceso o litros de más: se cobran %s por cada bloque de 1,000 litros o fracción que pase de los litros incluidos en el tipo de servicio. Vigente desde el %s.",
                        tarifa.nombre(), quetzales(tarifa.precio()), fecha(tarifa));
                resumen.add(String.format("exceso de consumo: %s por cada 1,000 litros o fracción",
                        quetzales(tarifa.precio())));
            }
            documentos.add(new Document(texto, metadatos(FUENTE_TARIFAS, tarifa.codigo())));
        }

        if (!resumen.isEmpty()) {
            String textoResumen = "Resumen de las tarifas vigentes de la oficina del agua (cuánto cuesta el servicio de agua): "
                    + String.join("; ", resumen) + ".";
            documentos.add(new Document(textoResumen, metadatos(FUENTE_TARIFAS, "RESUMEN")));
        }
        return documentos;
    }

    private Document documentoDeSectores(List<String> sectores) {
        String texto = "Sectores, barrios, colonias y aldeas donde hay servicio de agua (cobertura de la oficina del agua): "
                + String.join(", ", sectores) + ". En estos lugares la oficina del agua presta el servicio.";
        return new Document(texto, metadatos(FUENTE_SECTORES, "SECTORES"));
    }

    private Document documentoDeMetodosPago(List<String> metodos) {
        String texto = "Métodos o formas de pago que acepta la oficina del agua para pagar el servicio: "
                + metodos.stream().collect(Collectors.joining(", ")) + ".";
        return new Document(texto, metadatos(FUENTE_METODOS_PAGO, "METODOS_PAGO"));
    }

    private static Map<String, Object> metadatos(String fuente, String clave) {
        return Map.of("fuente", fuente, "origen", "replica", "clave", clave);
    }

    private static String quetzales(BigDecimal monto) {
        return String.format(Locale.US, "Q%,.2f", monto);
    }

    private static String litros(Integer cantidad) {
        return cantidad == null ? "0" : String.format(Locale.US, "%,d", cantidad);
    }

    private static String fecha(TarifaVigente tarifa) {
        return tarifa.vigenteDesde() == null ? "sin fecha" : tarifa.vigenteDesde().format(FORMATO_FECHA);
    }
}
