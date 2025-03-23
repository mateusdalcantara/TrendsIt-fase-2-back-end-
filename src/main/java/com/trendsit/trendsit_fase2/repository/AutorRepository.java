package com.trendsit.trendsit_fase2.repository;

import com.trendsit.trendsit_fase2.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository extends JpaRepository<Autor, String> {
}
