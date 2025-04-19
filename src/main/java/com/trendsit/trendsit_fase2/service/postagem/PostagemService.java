package com.trendsit.trendsit_fase2.repository.postagem;

import com.trendsit.trendsit_fase2.model.postagem.Postagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostagemService extends JpaRepository<Postagem, Long> {

    // --- BUSCA POR CONTEÚDO (CORREÇÃO DO ERRO) ---
    @Query("SELECT p FROM Postagem p WHERE p.conteudo LIKE %:keyword%")
    List<Postagem> searchByKeyword(@Param("keyword") String keyword);

    // --- BUSCA POR UUID (IDENTIFICADOR PÚBLICO) ---
    Optional<Postagem> findByUuid(String uuid);

    // --- BUSCA COM AUTOR (CARREGAMENTO "EAGER" PARA EVITAR LAZY LOADING) ---
    @Query("SELECT p FROM Postagem p LEFT JOIN FETCH p.autor WHERE p.id = :id")
    Optional<Postagem> findWithAuthorById(@Param("id") Long id);

    // --- BUSCA POSTAGENS DE USUÁRIOS SEGUIDOS (PARA O FEED) ---
    @Query("SELECT p FROM Postagem p WHERE p.autor.id IN :autorIds ORDER BY p.createdAt DESC")
    List<Postagem> findByAutorIdIn(@Param("autorIds") List<UUID> autorIds);
}