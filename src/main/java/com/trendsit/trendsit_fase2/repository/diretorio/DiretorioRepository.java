package com.trendsit.trendsit_fase2.repository.diretorio;

import com.trendsit.trendsit_fase2.model.diretorio.Diretorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiretorioRepository extends JpaRepository<Diretorio, Long> {
    @Query("SELECT d FROM Diretorio d " +
            "LEFT JOIN FETCH d.professor " +
            "LEFT JOIN FETCH d.alunos " +
            "WHERE d.id = :id")
    Optional<Diretorio> findByIdWithRelations(@Param("id") Long id);
}
