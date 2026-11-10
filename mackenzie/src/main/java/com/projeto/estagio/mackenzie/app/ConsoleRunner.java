package com.projeto.estagio.mackenzie.app;

import java.util.NoSuchElementException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Ponto de entrada da interface de console (substitui o antigo {@code Main}).
 *
 * Roda depois que o contexto do Spring sobe e abre o menu. Quando o usuário
 * sai do menu (ou a entrada acaba), encerra a aplicação, inclusive o servidor web embutido.
 *
 * Para subir só a parte web, sem o menu, use {@code --app.console.enabled=false}.
 */
@Component
@ConditionalOnProperty(name = "app.console.enabled", havingValue = "true", matchIfMissing = true)
public class ConsoleRunner implements CommandLineRunner {
    private final ApplicationContext contexto;
    private final MenuPrincipal menu;

    public ConsoleRunner(ApplicationContext contexto, MenuPrincipal menu) {
        this.contexto = contexto;
        this.menu = menu;
    }

    @Override
    public void run(String... args) {
        try {
            menu.abrir();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada. Até logo!");
        }
        encerrar(0);
    }

    private void encerrar(int codigoSaida) {
        System.exit(SpringApplication.exit(contexto, () -> codigoSaida));
    }
}
