package dao.jdbc;

import exception.PersistenciaException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Abre conexões JDBC com o PostgreSQL do Supabase. A senha vem de variável de ambiente. */
public class ConexaoSupabase {
    private static final String URL_PADRAO =
            "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require";
    private static final String USUARIO_PADRAO = "postgres.wcljsklgabcmxqjnyalf";

    private final String url;
    private final String usuario;
    private final String senha;

    public ConexaoSupabase() {
        this.url = lerVariavel("SUPABASE_JDBC_URL", URL_PADRAO);
        this.usuario = lerVariavel("SUPABASE_DB_USER", USUARIO_PADRAO);
        this.senha = System.getenv("SUPABASE_DB_PASSWORD");
        if (senha == null || senha.isBlank()) {
            throw new IllegalStateException(
                    "Configure SUPABASE_DB_PASSWORD. SUPABASE_JDBC_URL e SUPABASE_DB_USER "
                    + "são opcionais para substituir os padrões do projeto.");
        }
    }

    /** Lê uma variável de ambiente; se não existir, usa o valor padrão. */
    private static String lerVariavel(String nome, String padrao) {
        String valor = System.getenv(nome);
        return (valor == null || valor.isBlank()) ? padrao : valor;
    }

    public Connection abrir() {
        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            String causa = "28P01".equals(e.getSQLState())
                    ? "usuário ou senha do banco recusados"
                    : "verifique URL, usuário e rede (SQLState " + e.getSQLState() + ")";
            throw new PersistenciaException("Não foi possível conectar ao Supabase: " + causa + ".", e);
        }
    }
}
