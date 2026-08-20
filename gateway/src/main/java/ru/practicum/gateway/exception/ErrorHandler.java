package ru.practicum.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// ИМПОРТЫ ЗАМЕНЕНЫ НА JAKARTA ДЛЯ SPRING BOOT 3+
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationException(final IllegalArgumentException ex) {
        log.error("IllegalArgumentException {}", ex.toString());
        Map<String, String> map = new HashMap<>();
        String message = "Unknown state: UNSUPPORTED_STATUS";
        map.put("error", message);
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> constraintViolationException(ConstraintViolationException ex) {
        log.error("constraintViolationException  {}", ex.getMessage());
        Map<String, String> map = new HashMap<>();
        map.put("error", ex.getMessage());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> noItemUserException(NoSuchElementException ex) {
        log.error("noItemUserException {}", ex.getMessage());
        Map<String, String> map = new HashMap<>();
        map.put("error", ex.getMessage());
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    public ResponseEntity<Map<String, String>> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("methodArgumentNotValidException  {}", ex.getMessage());
        String defaultMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        Map<String, String> map = new HashMap<>();
        map.put("error", defaultMessage != null ? defaultMessage : "Ошибка валидации данных");
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleBookingException(final BookingException e) {
        log.error("Exception! BookingException: {}", e.getMessage());
        Map<String, String> map = new HashMap<>();
        map.put("error", e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> throwable(Throwable ex) {
        log.error("throwable {}", ex.toString());
        Map<String, String> map = new HashMap<>();
        map.put("error", ex.toString());
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}