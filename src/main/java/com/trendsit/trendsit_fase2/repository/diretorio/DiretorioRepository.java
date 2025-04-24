package com.trendsit.trendsit_fase2.repository.diretorio;

import com.trendsit.trendsit_fase2.model.diretorio.Diretorio;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface DiretorioRepository extends JpaRepository<Diretorio, Long> {

    Optional<Diretorio> findByTituloDoCurso(String tituloDoCurso);

    @Query("SELECT DISTINCT d FROM Diretorio d LEFT JOIN FETCH d.primaryProfessor LEFT JOIN FETCH d.alunos")
    List<Diretorio> findAllWithRelations();

    @Query("SELECT d FROM Diretorio d LEFT JOIN FETCH d.alunos WHERE d.id = :id")
    Optional<Diretorio> findByIdWithAlunos(@Param("id") Long id);



        @Query("SELECT d FROM Diretorio d " +
                "LEFT JOIN FETCH d.alunos " +
                "LEFT JOIN FETCH d.primaryProfessor " +
                "WHERE d.id = :id")
        Optional<Diretorio> findByIdWithRelations(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Profile p SET p.diretorio = NULL WHERE p.diretorio IN (SELECT d FROM Diretorio d WHERE d.primaryProfessor = :profile)")
    void removeProfileFromDiretorios(@Param("profile") Profile profile);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Diretorio d
         WHERE :profile MEMBER OF d.alunos
        """)
    void removeProfileFromAllDiretorios(@Param("profile") Profile profile);

}


