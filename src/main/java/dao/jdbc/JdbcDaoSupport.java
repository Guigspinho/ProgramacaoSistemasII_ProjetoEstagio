package dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import service.jdbc.ConexaoSupabase;

abstract class JdbcDaoSupport {
    @FunctionalInterface
    protected interface SqlOperation<T> {
        T execute(Connection connection) throws SQLException;
    }

    private final ConexaoSupabase conexoes;

    JdbcDaoSupport(ConexaoSupabase conexoes) {
        this.conexoes = conexoes;
    }

    protected <T> T consultar(SqlOperation<T> operation) throws SQLException {
        try (Connection connection = conexoes.abrir()) {
            return operation.execute(connection);
        }
    }

    protected <T> T transacionar(SqlOperation<T> operation) throws SQLException {
        try (Connection connection = conexoes.abrir()) {
            connection.setAutoCommit(false);
            try {
                T result = operation.execute(connection);
                connection.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackFailure) {
                    e.addSuppressed(rollbackFailure);
                }
                throw e;
            }
        }
    }

    protected static long executarInsertGerandoId(PreparedStatement statement) throws SQLException {
        statement.executeUpdate();
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) throw new SQLException("O banco não retornou o ID gerado.");
            return keys.getLong(1);
        }
    }
}
