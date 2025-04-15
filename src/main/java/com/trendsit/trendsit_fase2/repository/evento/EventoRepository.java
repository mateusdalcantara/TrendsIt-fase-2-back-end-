package com.trendsit.trendsit_fase2.repository.evento;

import com.trendsit.trendsit_fase2.model.evento.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    @Query("SELECT e FROM Evento e WHERE e.status = 'APROVADO' ORDER BY e.createdAt ASC")
    List<Evento> findAllApprovedEvents();

    @Query("SELECT e FROM Evento e WHERE e.status = 'PENDENTE' ORDER BY e.createdAt ASC")
    List<Evento> findAllPendingEvents();

    boolean existsByCodigoEvento(Long codigoEvento);

    @Query("SELECT e FROM Evento e LEFT JOIN FETCH e.autor WHERE e.codigoEvento = :codigoEvento")
    Optional<Evento> findByCodigoEvento(@Param("codigoEvento") Long codigoEvento);

    @Query("SELECT e FROM Evento e LEFT JOIN FETCH e.autor WHERE e.codigoEvento = :codigoEvento")
    Optional<Evento> findByCodigoEventoWithAutor(@Param("codigoEvento") Long codigoEvento);
}
