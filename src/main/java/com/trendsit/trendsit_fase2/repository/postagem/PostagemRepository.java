package com.trendsit.trendsit_fase2.repository.postagem;

import com.trendsit.trendsit_fase2.dto.postagem.PostagemResponseDTO;
import com.trendsit.trendsit_fase2.model.postagem.Postagem;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long> {

    @Query("SELECT DISTINCT p FROM Postagem p LEFT JOIN FETCH p.autor LEFT JOIN FETCH p.comentarios")
    List<Postagem> findAllWithComments();

    @EntityGraph(attributePaths = {"autor"})
    @Query("SELECT p FROM Postagem p WHERE p.conteudo LIKE %:keyword%")
    List<Postagem> searchByKeyword(String keyword);

    @EntityGraph(attributePaths = {"autor"})
    @Query("SELECT p FROM Postagem p LEFT JOIN FETCH p.autor WHERE p.id = :id")
    Optional<Postagem> findWithAuthorById(@Param("id") Long id);

    @Query("""
    SELECT p
    FROM Postagem p
    JOIN FETCH p.autor a
    LEFT JOIN FETCH p.comentarios c
    """)
    List<Postagem> findAllWithAuthorAndComments();

    @Query("SELECT p FROM Postagem p JOIN FETCH p.autor")
    List<Postagem> findAllWithAutor();

    @Query("SELECT p FROM Postagem p JOIN FETCH p.autor WHERE p.id = :id")
    Optional<Postagem> findByIdWithAutor(@Param("id") Long id);

    @Query("SELECT p FROM Postagem p LEFT JOIN FETCH p.comentarios WHERE p.autor.id IN :autorIds")
    List<Postagem> findByAutorIdWithComentarios(@Param("autorIds") List<UUID> autorIds);


    @Query("""
    SELECT new com.trendsit.trendsit_fase2.dto.postagem.PostagemResponseDTO(
       p.id,
       p.conteudo,
       p.createdAt,
       p.autor.username
    )
        FROM Postagem p
    """)
    List<PostagemResponseDTO> findAllPostsProjection();

    List<Postagem> findByAutor_IdIn(List<UUID> autorIds);

    @Query("SELECT p FROM Postagem p JOIN FETCH p.autor WHERE p.autor.id IN :autorIds")
    List<Postagem> findByAutorIdIn(@Param("autorIds") List<UUID> autorIds);

    @Query("SELECT p FROM Postagem p JOIN FETCH p.autor WHERE p.autor.id IN :autorIds")
    List<Postagem> findByAutorIdInWithAutor(@Param("autorIds") List<UUID> autorIds);

    void deleteByAutor(Profile autor);
}