package com.trendsit.trendsit_fase2.service.autor;

import com.trendsit.trendsit_fase2.model.autor.Autor;
import com.trendsit.trendsit_fase2.repository.autor.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public Autor findAutorById(UUID id) {
        return autorRepository.findById(String.valueOf(id)).orElse(null);
    }

    public Autor saveAutor(Autor autor) {
        return autorRepository.save(autor);
    }
}