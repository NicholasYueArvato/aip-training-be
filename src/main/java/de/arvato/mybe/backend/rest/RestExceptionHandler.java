package de.arvato.mybe.backend.rest;

import aep.core.starter.security.methodsecurity.ForbiddenException;
import de.arvato.mybe.backend.exception.BaseException;
import de.arvato.mybe.backend.general.ErrorCodes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler extends ResponseEntityExceptionHandler
{
    @ExceptionHandler(AccessDeniedException.class)
    protected void handleAccessDeniedException(HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException
    {
        logger.error("authentication required - " + e.getMessage(), e);

        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.getOutputStream().println("{ \"error\": \"authentication required - " + e.getMessage() + "\" }");
    }



    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity<Object> handleAEPForbiddenException(ForbiddenException ex)
    {
        return buildResponseEntity(new ApiError(HttpStatus.FORBIDDEN)
                .code(ErrorCodes.FORBIDDEN).message("Forbidden"));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    protected ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException ex)
    {
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR)
                .code(ErrorCodes.DUPLICATE_KEY_EXCEPTION).message("Duplication"));
    }

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<Object> handleBaseException(BaseException ex)
    {
        String errorCode = ErrorCodes.GENERAL_EXCEPTION;
        if(StringUtils.isNotEmpty(ex.getErrorCode()))
        {
            errorCode = ex.getErrorCode();
        }

        String errorMessage = ex.toString();
        if(StringUtils.isNotEmpty(ex.getMessage()))
        {
            errorMessage = ex.getMessage();
        }

        logger.error("BaseException occurred", ex);
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR)
                .code(errorCode).message(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGeneralException(Exception ex)
    {
        String message = "Unexpected exception occurred";
        logger.error(message, ex);
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR).message(message));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError)
    {
        return ResponseEntity.status(apiError.httpStatus)
                .body(RemoteResponse.fillResponseFailed(
                        apiError.code != null // Wishing Java had ?: operator :D
                                ? apiError.code
                                : ErrorCodes.GENERAL_EXCEPTION,
                        apiError.message));
    }



    private static class ApiError
    {
        private final HttpStatus httpStatus;
        private String code;
        private String message;

        public ApiError(HttpStatus httpStatus)
        {
            this.httpStatus = httpStatus;
        }

        public ApiError code(String errorCode)
        {
            this.code = errorCode;
            return this;
        }

        public ApiError message(String message)
        {
            this.message = message;
            return this;
        }

    }
}
