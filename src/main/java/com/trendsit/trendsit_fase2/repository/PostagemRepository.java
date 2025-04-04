package com.trendsit.trendsit_fase2.repository;

import com.trendsit.trendsit_fase2.model.Postagem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long> {

    @EntityGraph(attributePaths = {"comentarios", "autor"})
    @Query("SELECT p FROM Postagem p LEFT JOIN FETCH p.comentarios LEFT JOIN FETCH p.autor")
    List<Postagem> findAllWithComments();

    @EntityGraph(attributePaths = {"autor"})
    @Query("SELECT p FROM Postagem p WHERE p.titulo LIKE %:keyword%")
    List<Postagem> searchByKeyword(@Param("keyword") String keyword);

    @EntityGraph(attributePaths = {"autor"})
    @Query("SELECT p FROM Postagem p LEFT JOIN FETCH p.autor WHERE p.id = :id")
    Optional<Postagem> findWithAuthorById(@Param("id") Long id);
}