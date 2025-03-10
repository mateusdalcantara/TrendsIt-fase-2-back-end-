package com.trendsit.trendsit_fase2.exception;

//Manda ao solicitante um falso negativo. Ex: Email existe, informar que não tem.
public class UsuarioJaExisteException extends RuntimeException {
    public static String UsuarioJaExisteException(String message) {
        return message = "Erro ao cadastrar o email. Utilize um email alternativo.";
    }
}
