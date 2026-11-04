package com.projeto.estagio.mackenzie;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * O contexto usa o banco do Supabase, então este teste só roda quando SUPABASE_DB_PASSWORD
 * está definida. O menu de console fica desligado para o teste não ficar esperando o teclado.
 */
@SpringBootTest(properties = "app.console.enabled=false")
@EnabledIfEnvironmentVariable(named = "SUPABASE_DB_PASSWORD", matches = ".+")
class MackenzieApplicationTests {

	@Test
	void contextLoads() {
	}

}
