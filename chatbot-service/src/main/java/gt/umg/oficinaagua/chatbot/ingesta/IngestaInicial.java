package gt.umg.oficinaagua.chatbot.ingesta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IngestaInicial implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(IngestaInicial.class);

    private final IngestaService ingestaService;
    private final boolean alArrancar;

    public IngestaInicial(IngestaService ingestaService,
                          @Value("${chatbot.ingesta.al-arrancar:true}") boolean alArrancar) {
        this.ingestaService = ingestaService;
        this.alArrancar = alArrancar;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!alArrancar) {
            log.info("Ingesta al arrancar desactivada");
            return;
        }
        try {
            ingestaService.reindexar();
        } catch (RuntimeException e) {
            log.error("La ingesta inicial falló; el servicio arranca sin reindexar. Usa POST /api/ingesta cuando Ollama esté disponible.", e);
        }
    }
}