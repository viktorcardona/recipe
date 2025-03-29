package abn.recipe.api.handler;

import abn.recipe.exception.ObjectNotFoundException;
import abn.recipe.exception.ValidationException;
import abn.recipe.spec.spec.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(newErrorDto("An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorDto> handleObjectNotFoundException(ObjectNotFoundException ex) {
        log.warn(ex.getMessage(), ex);
        return new ResponseEntity<>(newErrorDto("Not Found", HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDto> handleValidationException(ValidationException ex) {
        log.warn(ex.getMessage() + ":" + ex.getErrorString(), ex);
        return new ResponseEntity<>(newErrorDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), ex.getErrorString()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn(ex.getMessage(), ex);
        return new ResponseEntity<>(newErrorDto("Argument validation error", HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorDto> handlePropertyReferenceException(PropertyReferenceException ex) {
        log.warn(ex.getMessage(), ex);
        return new ResponseEntity<>(newErrorDto("Property validation error", HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn(ex.getMessage(), ex);
        return new ResponseEntity<>(newErrorDto("Value validation error", HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn(ex.getMessage());

        var detail = new StringBuilder();
        var errorsResult = ex.getBindingResult();
        var fieldErrors = errorsResult.getFieldErrors();

        for (var fieldError : fieldErrors) {
            detail.append(fieldError.getObjectName())
                    .append(".")
                    .append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append(" ");
        }

        if (errorsResult.hasGlobalErrors()) {
            for (var globalError : errorsResult.getGlobalErrors()) {
                detail.append(globalError.getDefaultMessage());
            }
        }

        return new ResponseEntity<>(newErrorDto("Argument validation error", HttpStatus.BAD_REQUEST.value(), detail.toString()), HttpStatus.BAD_REQUEST);
    }

    private ErrorDto newErrorDto(String title, Integer statusCode, String detail) {
        var errorDto = new ErrorDto();
        errorDto.setTitle(title);
        errorDto.setStatus(statusCode);
        errorDto.setDetail(detail);
        return errorDto;
    }
}
