package exception;

/** O usuário não tem o perfil necessário ou não é dono do registro. */
public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String mensagem) { super(mensagem); }
}