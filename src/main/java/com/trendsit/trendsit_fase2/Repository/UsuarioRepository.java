package com.trendsit.trendsit_fase2.Repository;

import com.trendsit.trendsit_fase2.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}