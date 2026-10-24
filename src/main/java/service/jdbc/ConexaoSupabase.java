package service.jdbc;

import exception.PersistenciaException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Cria conexões JDBC com PostgreSQL/Supabase usando configuração externa. */
public final class ConexaoSupabase {
    private static final String URL_PADRAO =
            "jdbc:postgresql://db.wcljsklgabcmxqjnyalf.supabase.co:5432/postgres?sslmode=require";
    private static final String USUARIO_PADRAO = "postgres";

    private final String url;
    private final String usuario;
    private final String senha;

    public ConexaoSupabase() {
        this(
                valorOuPadrao("SUPABASE_JDBC_URL", URL_PADRAO),
                valorOuPadrao("SUPABASE_DB_USER", USUARIO_PADRAO),
                System.getenv("SUPABASE_DB_PASSWORD"));
    }

    ConexaoSupabase(String url, String usuario, String senha) {
        if (url == null || url.isBlank() || usuario == null || usuario.isBlank()
                || senha == null || senha.isBlank()) {
            throw new IllegalStateException(
                    "Configure SUPABASE_DB_PASSWORD. SUPABASE_JDBC_URL e SUPABASE_DB_USER "
                    + "são opcionais para substituir os padrões do projeto.");
        }
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    private static String valorOuPadrao(String variavel, String padrao) {
        String valor = System.getenv(variavel);
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    public Connection abrir() {
        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            String causa = switch (e.getSQLState() == null ? "" : e.getSQLState()) {
                case "28P01" -> "usuário ou senha do banco recusados";
                case "28000" -> "usuário sem autorização para acessar o banco";
                case "3D000" -> "banco de dados não encontrado";
                case "08001", "08006" -> "host, porta ou rede indisponíveis";
                default -> "falha JDBC (SQLState " + (e.getSQLState() == null
                        ? "desconhecido" : e.getSQLState()) + ")";
            };
            throw new PersistenciaException("Não foi possível conectar ao Supabase: " + causa + ".", e);
        }
    }
}
