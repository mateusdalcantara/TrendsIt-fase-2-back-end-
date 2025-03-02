package com.trendsit.trendsit_fase2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
public class TrendsitFase2Application {

	public static void main(String[] args) {SpringApplication.run(TrendsitFase2Application.class, args);
	}

	@RestController
	class Helloworld {
		@GetMapping("/")
		public String helloworld(){
			return "Hello world";
		}
	}
	@RestController
	@RequestMapping("/alunos")
	public class AlunoController {


		private AlunoRepository alunoRepository;

		@Autowired
		public void AlunoService(AlunoRepository alunoRepository) {
			this.alunoRepository = alunoRepository;
		}

		@PostMapping
		public Aluno criarAluno(@RequestBody Aluno aluno) {
			return alunoRepository.save(aluno);
		}

		@GetMapping
		public List<Aluno> listarAlunos() {
			return alunoRepository.findAll();
		}
	}

}
