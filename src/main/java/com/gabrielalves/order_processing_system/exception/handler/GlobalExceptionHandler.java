package com.gabrielalves.order_processing_system.exception.handler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.gabrielalves.order_processing_system.exception.ConflictException;
import com.gabrielalves.order_processing_system.exception.NotFoundException;
import com.gabrielalves.order_processing_system.exception.handler.ApiError.FieldViolation;

import lombok.extern.slf4j.Slf4j;

/**
 * Tratamento centralizado de erros.
 * <p>
 * Estende {@link ResponseEntityExceptionHandler} para que as exceções do próprio Spring MVC
 * (405, 415, rota inexistente etc.) mantenham o status correto, mas todas as respostas
 * seguem o mesmo formato {@link ApiError}.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflict(ConflictException ex, WebRequest request) {
        log.warn("Conflito de regra de negócio: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
    }

    // Última barreira: constraints do banco (unique, FK, check) violadas, ex.: dois cadastros simultâneos com o mesmo e-mail.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        log.warn("Violação de integridade no banco: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "A operação viola uma restrição de integridade dos dados", request, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception ex, WebRequest request) {
        log.error("Erro inesperado ao processar {}", path(request), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado", request, List.of());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldViolation> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldViolation(error.getField(), error.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Dados de entrada inválidos", request, fields);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Corpo da requisição ausente ou malformado", request, List.of());
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String parameter = ex instanceof MethodArgumentTypeMismatchException mismatch
                ? mismatch.getName()
                : ex.getPropertyName();
        String message = "Valor inválido para o parâmetro '%s': %s".formatted(parameter, ex.getValue());
        return build(HttpStatus.BAD_REQUEST, message, request, List.of());
    }

    // Demais exceções do Spring MVC: mantém o status definido pelo framework e converte para ApiError.
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
            HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        String message = body instanceof ProblemDetail problem && problem.getDetail() != null
                ? problem.getDetail()
                : ex.getMessage();
        ApiError error = apiError(statusCode, message, request, List.of());
        return ResponseEntity.status(statusCode).headers(headers).body(error);
    }

    private ResponseEntity<Object> build(HttpStatus status, String message, WebRequest request,
            List<FieldViolation> fields) {
        return ResponseEntity.status(status).body(apiError(status, message, request, fields));
    }

    private ApiError apiError(HttpStatusCode statusCode, String message, WebRequest request,
            List<FieldViolation> fields) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        String reason = status != null ? status.getReasonPhrase() : String.valueOf(statusCode.value());
        return new ApiError(LocalDateTime.now(), statusCode.value(), reason, message, path(request), fields);
    }

    private static String path(WebRequest request) {
        return request instanceof ServletWebRequest servletRequest
                ? servletRequest.getRequest().getRequestURI()
                : null;
    }
}
