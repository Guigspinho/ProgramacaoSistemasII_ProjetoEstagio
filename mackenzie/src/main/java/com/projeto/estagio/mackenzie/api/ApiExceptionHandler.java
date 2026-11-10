package com.projeto.estagio.mackenzie.api;

import com.projeto.estagio.mackenzie.api.ApiDtos.ErroResponse;
import com.projeto.estagio.mackenzie.exception.AcessoNegadoException;
import com.projeto.estagio.mackenzie.exception.RecursoNaoEncontradoException;
import com.projeto.estagio.mackenzie.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(
            RecursoNaoEncontradoException exception, HttpServletRequest request) {
        return erro(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroResponse> acessoNegado(
            AcessoNegadoException exception, HttpServletRequest request) {
        return erro(HttpStatus.FORBIDDEN, exception.getMessage(), request);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> conflito(
            RegraNegocioException exception, HttpServletRequest request) {
        return erro(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> entradaInvalida(
            IllegalArgumentException exception, HttpServletRequest request) {
        return erro(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestHeaderException.class
    })
    public ResponseEntity<ErroResponse> requisicaoInvalida(
            Exception exception, HttpServletRequest request) {
        return erro(HttpStatus.BAD_REQUEST, "Requisição inválida ou incompleta.", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> conflitoDePersistencia(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        return erro(HttpStatus.CONFLICT, "A operação viola uma restrição de integridade.", request);
    }

    private ResponseEntity<ErroResponse> erro(
            HttpStatus status, String mensagem, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErroResponse(
                Instant.now(), status.value(), status.getReasonPhrase(), mensagem, request.getRequestURI()));
    }
}
