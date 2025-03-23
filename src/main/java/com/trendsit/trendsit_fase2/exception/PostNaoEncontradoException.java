package com.trendsit.trendsit_fase2.exception;

public class PostNaoEncontradoException extends RuntimeException {
    public PostNaoEncontradoException(Long postId) {
        super("Postagem não encontrada para o ID: " + postId);
    }
}
