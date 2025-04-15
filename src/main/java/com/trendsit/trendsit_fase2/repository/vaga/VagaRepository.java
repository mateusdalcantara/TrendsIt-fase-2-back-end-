package com.trendsit.trendsit_fase2.repository.vaga;

import com.trendsit.trendsit_fase2.model.vaga.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    @Query("SELECT v FROM Vaga v WHERE v.status = 'APROVADO' ORDER BY v.createdAt DESC")
    List<Vaga> findAllByStatus(Vaga.Status status);

    @Query("SELECT v FROM Vaga v WHERE v.status = 'PENDENTE' ORDER BY v.createdAt ASC")
    List<Vaga> findAllPending();

    boolean existsByCodigoVaga(Long codigo);
    Optional<Vaga> findByCodigoVaga(Long codigoVaga);

}