package com.trendsit.trendsit_fase2.repository.autor;

import com.trendsit.trendsit_fase2.model.autor.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository extends JpaRepository<Autor, String> {
}
