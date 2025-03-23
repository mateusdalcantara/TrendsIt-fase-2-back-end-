package com.trendsit.trendsit_fase2.exception;

import java.util.UUID;

public class ProfileNaoEncontradoException extends RuntimeException {
    public ProfileNaoEncontradoException(UUID userId) {
        super("Perfil não encontrado para o ID: " + userId);
    }
}