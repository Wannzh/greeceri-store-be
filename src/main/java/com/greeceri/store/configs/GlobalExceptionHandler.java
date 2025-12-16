package com.greeceri.store.configs;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.greeceri.store.models.response.GeneralResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Handle Error Validasi Input (@Valid, @NotBlank, dll)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralResponse> handleValidationErrors(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GeneralResponse(false, "Validation Failed: " + errorMessage));
    }

    // Handle Runtime Exception (Data tidak ditemukan, Stok habis, dll)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GeneralResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GeneralResponse(false, ex.getMessage()));
    }

    // Handle Exception Umum (NullPointer, Database Down, dll)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponse> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GeneralResponse(false, "An internal error occurred: " + ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(
            IllegalStateException ex) {

        return ResponseEntity
                .badRequest()
                .body(Map.of("message", ex.getMessage()));
    }
}
