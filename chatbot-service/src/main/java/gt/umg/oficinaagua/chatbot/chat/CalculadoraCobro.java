package gt.umg.oficinaagua.chatbot.chat;

import gt.umg.oficinaagua.chatbot.replica.CatalogoReplicaRepository;
import gt.umg.oficinaagua.chatbot.replica.TarifaVigente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CalculadoraCobro {

    private static final Logger log = LoggerFactory.getLogger(CalculadoraCobro.class);

    private static final Pattern LITROS = Pattern.compile(
            "(?<![\\d.,])(\\d{1,3}(?:[.,]\\d{3})+|\\d+)\\s*(?:litros?|lts?)(?!\\p{L})",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private static final Pattern METROS_CUBICOS = Pattern.compile(
            "(\\d+(?:[.,]\\d+)?)\\s*(?:m3|m³|metros? c[uú]bicos?)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private final CatalogoReplicaRepository repositorio;

    public CalculadoraCobro(CatalogoReplicaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Optional<String> calcular(String pregunta) {
        String codigo = detectarTipoServicio(pregunta);
        Long consumo = detectarConsumo(pregunta);
        if (codigo == null || consumo == null) {
            return Optional.empty();
        }

        List<TarifaVigente> tarifas;
        try {
            tarifas = repositorio.buscarTarifasVigentes();
        } catch (DataAccessException e) {
            log.warn("No se pudo consultar la réplica para calcular el cobro: {}", e.getMessage());
            return Optional.empty();
        }

        TarifaVigente servicio = tarifas.stream()
                .filter(tarifa -> tarifa.esServicio() && codigo.equals(tarifa.codigo()))
                .findFirst()
                .orElse(null);
        TarifaVigente exceso = tarifas.stream()
                .filter(tarifa -> !tarifa.esServicio())
                .findFirst()
                .orElse(null);
        if (servicio == null || exceso == null || servicio.volumenIncluidoLitros() == null) {
            return Optional.empty();
        }

        long incluidos = servicio.volumenIncluidoLitros();
        if (consumo <= incluidos) {
            return Optional.of(String.format(Locale.US,
                    "Servicio %s con un consumo de %,d litros. El servicio incluye %,d litros al mes, así que el consumo no pasó de los litros incluidos y no hay exceso. Total a pagar: %s (solo la cuota mensual).",
                    servicio.nombre(), consumo, incluidos, quetzales(servicio.precio())));
        }

        long litrosDeMas = consumo - incluidos;
        long bloques = (litrosDeMas + 999) / 1000;
        BigDecimal cargoExceso = exceso.precio().multiply(BigDecimal.valueOf(bloques));
        BigDecimal total = servicio.precio().add(cargoExceso);

        return Optional.of(String.format(Locale.US,
                "Servicio %s con un consumo de %,d litros. Litros incluidos: %,d. Litros de más: %,d - %,d = %,d. "
                        + "Bloques de exceso: %,d litros entre 1,000, redondeado hacia arriba = %d. "
                        + "Cargo por exceso: %d × %s = %s. Cuota mensual: %s. Total a pagar: %s + %s = %s.",
                servicio.nombre(), consumo, incluidos, consumo, incluidos, litrosDeMas,
                litrosDeMas, bloques,
                bloques, quetzales(exceso.precio()), quetzales(cargoExceso), quetzales(servicio.precio()),
                quetzales(servicio.precio()), quetzales(cargoExceso), quetzales(total)));
    }

    private static String detectarTipoServicio(String pregunta) {
        String texto = pregunta.toLowerCase(Locale.ROOT);
        if (texto.contains("media paja") || texto.contains("medio paja") || texto.contains("1/2")) {
            return "MEDIA_PAJA";
        }
        if (texto.contains("cuarto de paja") || texto.contains("cuarto paja") || texto.contains("1/4")) {
            return "CUARTO_PAJA";
        }
        return null;
    }

    private static Long detectarConsumo(String pregunta) {
        Matcher litros = LITROS.matcher(pregunta);
        if (litros.find()) {
            return Long.parseLong(litros.group(1).replaceAll("[.,]", ""));
        }
        Matcher metros = METROS_CUBICOS.matcher(pregunta);
        if (metros.find()) {
            double valor = Double.parseDouble(metros.group(1).replace(',', '.'));
            return Math.round(valor * 1000);
        }
        return null;
    }

    private static String quetzales(BigDecimal monto) {
        return String.format(Locale.US, "Q%,.2f", monto);
    }
}
