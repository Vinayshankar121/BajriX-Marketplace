package com.bajrix.marketplace.exception;

import org.springframework.http.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(NotFoundException e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage())); }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> conflict(ConflictException e) { return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage())); }
    @ExceptionHandler({DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<Map<String, String>> persistenceConflict(Exception e) { return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "The data changed or conflicts with another update. Refresh and try again.")); }
    @ExceptionHandler({IllegalArgumentException.class, org.springframework.web.bind.MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> badRequest(Exception e) { return ResponseEntity.badRequest().body(Map.of("message", "Invalid request: " + e.getMessage())); }
}
