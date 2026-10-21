package exception;

    /** Violação de uma regra de negócio (ex.: e-mail repetido, vaga encerrada, candidatura duplicada). */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) { super(mensagem); }

}
