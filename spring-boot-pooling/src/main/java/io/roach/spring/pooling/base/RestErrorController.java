package io.roach.spring.pooling.base;

import java.lang.reflect.UndeclaredThrowableException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

@RestControllerAdvice
@Controller
public class RestErrorController extends ResponseEntityExceptionHandler implements ErrorController {
    @RequestMapping("/error")
    public ResponseEntity<Object> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus;
        if (status != null) {
            httpStatus = HttpStatus.valueOf(Integer.parseInt(status.toString()));
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return wrap(Problem.create()
                .withStatus(httpStatus)
                .withTitle(httpStatus.getReasonPhrase()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        Problem problem = Problem.create()
                .withDetail(ex.getLocalizedMessage())
                .withStatus(HttpStatus.BAD_REQUEST)
                .withProperties(map -> {
                    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
                        map.put(error.getField(), error.getDefaultMessage());
                    }
                    for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
                        map.put(error.getObjectName(), error.getDefaultMessage());
                    }
                });

        return handleExceptionInternal(ex, problem, headers, problem.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
                                                         HttpStatus status, WebRequest request) {

        Problem problem = Problem.create()
                .withDetail(ex.getLocalizedMessage())
                .withStatus(HttpStatus.BAD_REQUEST)
                .withProperties(map -> {
                    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
                        map.put(error.getField(), error.getDefaultMessage());
                    }
                    for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
                        map.put(error.getObjectName(), error.getDefaultMessage());
                    }
                });

        return handleExceptionInternal(ex, problem, headers, problem.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatus status, WebRequest request) {

        String error =
                ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();

        Problem problem = Problem.create()
                .withDetail(ex.getLocalizedMessage())
                .withStatus(HttpStatus.BAD_REQUEST)
                .withProperties(error);

        return wrap(problem);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
                                                                     HttpHeaders headers, HttpStatus status,
                                                                     WebRequest request) {
        String error = ex.getRequestPartName() + " part is missing";

        Problem problem = Problem.create()
                .withDetail(ex.getLocalizedMessage())
                .withStatus(HttpStatus.BAD_REQUEST)
                .withProperties(error);

        return wrap(problem);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {

        String error = ex.getParameterName() + " parameter is missing";

        Problem problem = Problem.create()
                .withDetail(ex.getLocalizedMessage())
                .withStatus(HttpStatus.BAD_REQUEST)
                .withProperties(error);

        return wrap(problem);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   HttpHeaders headers, HttpStatus status,
                                                                   WebRequest request) {
        String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

        Problem problem = Problem.create()
                .withDetail(ex.getLocalizedMessage())
                .withStatus(HttpStatus.NOT_FOUND)
                .withProperties(error);

        return wrap(problem);
    }

    // 405

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {

        StringBuilder sb = new StringBuilder();
        sb.append(ex.getMethod());
        sb.append(" method is not supported for this request. Supported methods are ");

        if (ex.getSupportedHttpMethods() != null) {
            ex.getSupportedHttpMethods().forEach(t -> sb.append(t).append(" "));
        }

        Problem problem = Problem.create()
                .withDetail(ex.getLocalizedMessage())
                .withStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .withProperties(sb.toString());

        return wrap(problem);
    }

    // 415

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers, HttpStatus status,
                                                                     WebRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getContentType());
        sb.append(" media type is not supported. Supported media types are ");

        if (ex.getSupportedMediaTypes() != null) {
            ex.getSupportedMediaTypes().forEach(t -> sb.append(t).append(" "));
        }

        Problem problem = Problem.create()
                .withDetail(ex.getLocalizedMessage())
                .withStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .withProperties(sb.toString());

        return wrap(problem);
    }


    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        if (status.is5xxServerError()) {
            logger.error("", ex);
        } else {
            logger.warn("", ex);
        }

        if (body instanceof Problem) {
            return wrap(Problem.class.cast(body));
        }

        Problem problem = Problem.create()
                .withDetail(ex.getLocalizedMessage())
                .withTitle(status.getReasonPhrase())
                .withStatus(status)
                .withProperties(body);

        return wrap(problem);
    }

    protected ResponseEntity<Object> wrap(Problem problem) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return ResponseEntity
                .status(problem.getStatus())
                .headers(headers)
                .body(problem);
    }

    // 500

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Throwable ex, WebRequest request) {
        if (ex instanceof UndeclaredThrowableException) {
            ex = ((UndeclaredThrowableException) ex).getUndeclaredThrowable();
        }

        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            if (responseStatus.code().is5xxServerError()) {
                logger.error("", ex);
            }

            Problem problem = Problem.create()
                    .withDetail(NestedExceptionUtils.getMostSpecificCause(ex).toString())
                    .withStatus(responseStatus.value());

            return wrap(problem);
        }

        logger.error("", ex);

        Problem problem = Problem.create()
                .withDetail(NestedExceptionUtils.getMostSpecificCause(ex).toString())
                .withTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        return wrap(problem);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                   WebRequest request) {

        String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();

        Problem problem = Problem.create()
                .withDetail(ex.getLocalizedMessage())
                .withStatus(HttpStatus.BAD_REQUEST)
                .withProperties(error);

        return wrap(problem);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                 WebRequest request) {

        Problem problem = Problem.create()
                .withDetail(ex.getLocalizedMessage())
                .withStatus(HttpStatus.BAD_REQUEST)
                .withTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());

        return wrap(problem);
    }
}
