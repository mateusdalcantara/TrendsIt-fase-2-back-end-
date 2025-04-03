package com.trendsit.trendsit_fase2.repository;

import com.trendsit.trendsit_fase2.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    @Query("SELECT e FROM Evento e WHERE e.status = 'APROVADO' ORDER BY e.createdAt ASC")
    List<Evento> findAllApprovedEvents();

    @Query("SELECT e FROM Evento e WHERE e.status = 'PENDENTE' ORDER BY e.createdAt ASC")
    List<Evento> findAllPendingEvents();
}
