package com.example.demo.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ListaNoVaciaException extends RuntimeException {
    public ListaNoVaciaException(String message) {
        super(message);
    }
}
