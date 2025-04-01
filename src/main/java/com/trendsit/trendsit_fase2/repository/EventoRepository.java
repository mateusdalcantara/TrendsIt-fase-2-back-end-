package com.trendsit.trendsit_fase2.repository;

import com.trendsit.trendsit_fase2.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento,Long> {
    List<Evento> findAllByStatusTrue();
}
