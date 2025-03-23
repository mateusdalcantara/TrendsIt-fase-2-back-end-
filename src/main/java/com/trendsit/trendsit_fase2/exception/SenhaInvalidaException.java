package com.trendsit.trendsit_fase2.exception;

public class SenhaInvalidaException extends RuntimeException {
    public SenhaInvalidaException() {
        super("Senha deve ter mais que seis(6) caracteres.");
    }
}
