package dao.jdbc;

import exception.PersistenciaException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Executa o script SQL (tabelas e dados fictícios) ao iniciar a aplicação. */
public class InicializadorBanco {
    private static final String SCRIPT = "/db/estrutura-e-dados.sql";

    private final ConexaoSupabase conexao;

    public InicializadorBanco(ConexaoSupabase conexao) {
        this.conexao = conexao;
    }

    /** O script só usa IF NOT EXISTS / ON CONFLICT, então pode ser executado várias vezes. */
    public void inicializar() {
        String script = lerScript();
        try (Connection con = conexao.abrir();
             Statement st = con.createStatement()) {
            for (String comando : script.split(";")) {
                if (!comando.isBlank()) {
                    st.execute(comando);
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Não foi possível inicializar o esquema e os dados do banco.", e);
        }
    }

    private String lerScript() {
        try (InputStream in = InicializadorBanco.class.getResourceAsStream(SCRIPT)) {
            if (in == null) {
                throw new PersistenciaException("Script SQL não encontrado: " + SCRIPT, null);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível ler o script SQL do banco.", e);
        }
    }
}
