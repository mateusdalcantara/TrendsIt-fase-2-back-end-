package com.trendsit.trendsit_fase2.repository.diretorio;

import com.trendsit.trendsit_fase2.model.diretorio.Diretorio;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiretorioRepository extends JpaRepository<Diretorio, Long> {

    Optional<Diretorio> findByTituloDoCurso(String tituloDoCurso);

    @Query("SELECT DISTINCT d FROM Diretorio d LEFT JOIN FETCH d.primaryProfessor LEFT JOIN FETCH d.alunos")
    List<Diretorio> findAllWithRelations();

    @Query("SELECT d FROM Diretorio d LEFT JOIN FETCH d.alunos WHERE d.id = :id")
    Optional<Diretorio> findByIdWithAlunos(@Param("id") Long id);

}
