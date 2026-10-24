package dao.jdbc;

import exception.PersistenciaException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import service.jdbc.ConexaoSupabase;

/** Executa o esquema e os dados fictícios definidos no script SQL distribuído com o projeto. */
public final class InicializadorBanco {
    private static final String SCRIPT = "/db/estrutura-e-dados.sql";

    private final ConexaoSupabase conexoes;

    public InicializadorBanco(ConexaoSupabase conexoes) {
        this.conexoes = conexoes;
    }

    public void inicializar() {
        String script = carregarScript();
        try (Connection connection = conexoes.abrir()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                for (String sql : script.split(";")) {
                    if (!sql.isBlank()) statement.execute(sql);
                }
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackFailure) {
                    e.addSuppressed(rollbackFailure);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Não foi possível inicializar o esquema e os dados do banco.", e);
        }
    }

    private String carregarScript() {
        try (InputStream resource = InicializadorBanco.class.getResourceAsStream(SCRIPT)) {
            if (resource == null) throw new PersistenciaException(
                    "Script SQL não encontrado no classpath: " + SCRIPT, null);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource, StandardCharsets.UTF_8))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) content.append(line).append('\n');
                return content.toString();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível ler o script SQL do banco.", e);
        }
    }
}
