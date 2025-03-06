package com.trendsit.trendsit_fase2.Controller;

import com.trendsit.trendsit_fase2.Service.UsuarioService;
import com.trendsit.trendsit_fase2.dto.RegisterDTO;
import com.trendsit.trendsit_fase2.dto.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    @Autowired
    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios(){
        List<UsuarioDTO> usuario = usuarioService.FindAll();
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> saveUsuario(@RequestBody RegisterDTO registerDTO){
        UsuarioDTO usuarioDTO = usuarioService.saveUser(registerDTO);
        return ResponseEntity.ok(usuarioDTO);
    }

}
