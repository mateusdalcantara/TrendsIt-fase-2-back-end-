package com.trendsit.trendsit_fase2.repository.comentario;

import com.trendsit.trendsit_fase2.model.comentario.Comentario;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    @Query("SELECT c FROM Comentario c LEFT JOIN FETCH c.autor WHERE c.id = :id")
    Optional<Comentario> findWithAutorById(@Param("id") Long id);

    @Query("SELECT c FROM Comentario c WHERE c.postagem.id = :postId AND c.autor.id = :autorId")
    List<Comentario> findByPostagemIdAndAutorId(@Param("postId") Long postId, @Param("autorId") UUID autorId);

    @Query("SELECT c FROM Comentario c LEFT JOIN FETCH c.postagem WHERE c.autor.id = :autorId")
    List<Comentario> findByAutorId(@Param("autorId") UUID autorId);

    @Query("SELECT c FROM Comentario c WHERE c.postagem.id = :postId")
    List<Comentario> findByPostagemId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comentario c JOIN FETCH c.autor WHERE c.autor.id IN :autorIds")
    List<Comentario> findByAutorIdIn(@Param("autorIds") List<UUID> autorIds);
}

