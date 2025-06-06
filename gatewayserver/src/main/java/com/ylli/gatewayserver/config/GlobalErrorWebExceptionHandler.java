//package com.ylli.gatewayserver.config;
//
//import com.ylli.shared.dtos.ErrorResponseDto;
//import com.ylli.shared.exceptions.*;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.ConstraintViolationException;
//import org.springframework.boot.autoconfigure.web.WebProperties;
//import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
//import org.springframework.boot.web.reactive.error.ErrorAttributes;
//import org.springframework.context.ApplicationContext;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.codec.ServerCodecConfigurer;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.stereotype.Component;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.support.WebExchangeBindException;
//import org.springframework.web.reactive.function.server.*;
//import reactor.core.publisher.Mono;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Component
//public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {
//
//    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes,
//                                          WebProperties webProperties,
//                                          ApplicationContext applicationContext,
//                                          ServerCodecConfigurer serverCodecConfigurer) {
//        super(errorAttributes, webProperties.getResources(), applicationContext);
//        this.setMessageWriters(serverCodecConfigurer.getWriters());
//        this.setMessageReaders(serverCodecConfigurer.getReaders());
//    }
//
//    @Override
//    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
//        // Handle all requests and delegate to renderErrorResponse
//        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
//    }
//
//    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
//        Throwable error = getError(request);
//
//        ErrorResponseDto errorResponse = buildErrorResponse(error, request);
//
//        HttpStatus status = determineHttpStatus(error);
//
//        return ServerResponse.status(status)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(errorResponse);
//    }
//
//    private ErrorResponseDto buildErrorResponse(Throwable ex, ServerRequest request) {
//        ErrorResponseDto.ErrorResponseDtoBuilder builder = ErrorResponseDto.builder()
//                .timestamp(LocalDateTime.now())
//                .path(request.path())
//                .traceId(UUID.randomUUID().toString());
//
//        if (ex instanceof EntityNotFoundException || ex instanceof ResourceNotFoundException) {
//            builder.status(HttpStatus.NOT_FOUND.value())
//                    .error(HttpStatus.NOT_FOUND.getReasonPhrase())
//                    .message(ex.getMessage());
//        } else if (ex instanceof WebExchangeBindException) {
//            // This is equivalent to MethodArgumentNotValidException in MVC
//            WebExchangeBindException webEx = (WebExchangeBindException) ex;
//
//            builder.status(HttpStatus.BAD_REQUEST.value())
//                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
//                    .message("Validation error");
//
//            ErrorResponseDto response = builder.build();
//            webEx.getBindingResult().getFieldErrors().forEach(fieldError -> {
//                response.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
//            });
//            return response;
//
//        } else if (ex instanceof ConstraintViolationException) {
//            ConstraintViolationException cve = (ConstraintViolationException) ex;
//
//            builder.status(HttpStatus.BAD_REQUEST.value())
//                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
//                    .message("Validation error");
//
//            ErrorResponseDto response = builder.build();
//            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
//                response.addValidationError(
//                        violation.getPropertyPath().toString(),
//                        violation.getMessage());
//            }
//            return response;
//
//        } else if (ex instanceof DataIntegrityViolationException || ex instanceof DuplicateResourceException) {
//            builder.status(HttpStatus.CONFLICT.value())
//                    .error(HttpStatus.CONFLICT.getReasonPhrase())
//                    .message(ex.getMessage() != null ? ex.getMessage() : "Database constraint violation");
//        } else if (ex instanceof ValidationException || ex instanceof InsufficientFundsException || ex instanceof TransactionFailedException) {
//            builder.status(HttpStatus.BAD_REQUEST.value())
//                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
//                    .message(ex.getMessage());
//        } else if (ex instanceof AccountLockedException) {
//            builder.status(HttpStatus.LOCKED.value())
//                    .error(HttpStatus.LOCKED.getReasonPhrase())
//                    .message(ex.getMessage());
//        } else if (ex instanceof AccessDeniedException) {
//            builder.status(HttpStatus.FORBIDDEN.value())
//                    .error(HttpStatus.FORBIDDEN.getReasonPhrase())
//                    .message(ex.getMessage());
//        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
//            builder.status(HttpStatus.METHOD_NOT_ALLOWED.value())
//                    .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
//                    .message(ex.getMessage());
//        } else {
//            // fallback for all other exceptions
//            builder.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
//                    .message("An unexpected error occurred");
//        }
//
//        return builder.build();
//    }
//
//    private HttpStatus determineHttpStatus(Throwable ex) {
//        if (ex instanceof EntityNotFoundException || ex instanceof ResourceNotFoundException) {
//            return HttpStatus.NOT_FOUND;
//        } else if (ex instanceof WebExchangeBindException || ex instanceof ConstraintViolationException
//                || ex instanceof ValidationException || ex instanceof InsufficientFundsException
//                || ex instanceof TransactionFailedException) {
//            return HttpStatus.BAD_REQUEST;
//        } else if (ex instanceof DataIntegrityViolationException || ex instanceof DuplicateResourceException) {
//            return HttpStatus.CONFLICT;
//        } else if (ex instanceof AccountLockedException) {
//            return HttpStatus.LOCKED;
//        } else if (ex instanceof AccessDeniedException) {
//            return HttpStatus.FORBIDDEN;
//        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
//            return HttpStatus.METHOD_NOT_ALLOWED;
//        }
//        return HttpStatus.INTERNAL_SERVER_ERROR;
//    }
//}
