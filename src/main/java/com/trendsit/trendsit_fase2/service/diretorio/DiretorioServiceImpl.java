package com.trendsit.trendsit_fase2.service.diretorio;

import com.trendsit.trendsit_fase2.dto.diretorio.*;

import com.trendsit.trendsit_fase2.dto.profile.ProfileResponseDTO;
import com.trendsit.trendsit_fase2.exception.CustomExceptions;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.model.diretorio.Diretorio;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.diretorio.DiretorioRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DiretorioServiceImpl implements DiretorioService {
    private final DiretorioRepository diretorioRepository;

    private final ProfileRepository profileRepository;

    public DiretorioServiceImpl(DiretorioRepository diretorioRepository, ProfileRepository profileRepository) {
        this.diretorioRepository = diretorioRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public List<DiretorioDTO> findAllDiretorio() {
        List<Diretorio> diretorios = diretorioRepository.findAllWithRelations();
        return diretorios.stream()
                .map(DiretorioDTO::new)
                .collect(Collectors.toList());
    }

    // DiretorioServiceImpl.java
    @Override
    public List<Profile> getAlunos() {
        Diretorio diretorio = diretorioRepository.findByTituloDoCurso("Turma A") // Updated method name
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        return new ArrayList<>(diretorio.getAlunos());
    }

    // Cria o diretório
    public Diretorio criarDiretorio(DiretorioRequestDTO request) {
        Diretorio diretorio = new Diretorio();
        diretorio.setTurma(request.getTurma());
        diretorio.setTituloDoCurso(request.getTituloDoCurso());
        return diretorioRepository.save(diretorio);
    }


    @Override
    public TurmaAlunoDTO listarMeusColegas(UUID alunoId) {
        Profile aluno = profileRepository.findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        // Verificação de papel
        if (aluno.getRole() != ProfileRole.ALUNO) {
            throw new AccessDeniedException("Acesso permitido apenas para alunos");
        }

        // Busca o diretório COM alunos
        Diretorio dir = diretorioRepository
                .findByIdWithAlunos(aluno.getDiretorio().getId())
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        // Filtra e mapeia colegas
        List<AlunoDTO> colegas = dir.getAlunos().stream()
                .filter(p -> p.getRole() == ProfileRole.ALUNO)
                .map(p -> new AlunoDTO(
                        p.getUsername(),
                        dir.getTituloDoCurso(), // Usa o título do diretório
                        p.getFriendNumber(),
                        p.getCreatedAt()
                ))
                .toList();

        return new TurmaAlunoDTO(dir.getTurma(), colegas);
    }



    public List<ProfileResponseDTO> FindProfileAllSameClass(UUID requesterId) {
        Profile requester = profileRepository.findById(requesterId).orElseThrow(()->new EntityNotFoundException("Perfil não achado"));

        if (requester.getRole() == ProfileRole.ALUNO) {
            if (requester.getDiretorio() == null) {
                throw new IllegalStateException("Aluno não vinculado a uma turma");
            }
            return profileRepository.findAllByDiretorioId(requester.getDiretorio().getId())
                    .stream()
                    .map(ProfileResponseDTO::new)
                    .collect(Collectors.toList());
        }
        // Se for professor, retorna todos os alunos de todos os diretórios
        else if (requester.getRole() == ProfileRole.PROFESSOR) {
            return profileRepository.findAll().stream()
                    .filter(p -> p.getDiretorio() != null)
                    .map(ProfileResponseDTO::new)
                    .collect(Collectors.toList());
        }
        throw new AccessDeniedException("Acesso negado");
    }


    public ProfileResponseDTO findProfileByIdSameClass(UUID targetId, UUID requesterId) {
        Profile requester = profileRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil solicitante não encontrado"));

        Profile target = profileRepository.findById(targetId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil alvo não encontrado"));

        // Verifica se o solicitante tem acesso
        if (requester.getRole() == ProfileRole.ALUNO) {
            if (requester.getDiretorio() == null || target.getDiretorio() == null ||
                    requester.getDiretorio().getId() != target.getDiretorio().getId()) {
                throw new AccessDeniedException("Acesso não permitido à este perfil");
            }
        }
        // Professores podem acessar qualquer perfil
        else if (requester.getRole() != ProfileRole.PROFESSOR) {
            throw new AccessDeniedException("Acesso negado");
        }

        return new ProfileResponseDTO(target);
    }

    public ProfileResponseDTO findProfileByFriendNumberSameClass(Long friendNumber, UUID requesterId) {
        Profile requester = profileRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil solicitante não encontrado"));

        Profile target = profileRepository.findByFriendNumber(friendNumber)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        // Mesma lógica de verificação
        if (requester.getRole() == ProfileRole.ALUNO) {
            if (requester.getDiretorio() == null || target.getDiretorio() == null ||
                    requester.getDiretorio().getId() != target.getDiretorio().getId()) {
                throw new AccessDeniedException("Acesso não permitido à este perfil ");
            }
        }
        else if (requester.getRole() != ProfileRole.PROFESSOR) {
            throw new AccessDeniedException("Acesso negado");
        }

        return new ProfileResponseDTO(target);
    }

    public void CreateDirectory(String Turma){
        Diretorio diretorio = new Diretorio();
        diretorio.setTurma(Turma);
        diretorioRepository.save(diretorio);
    }

    @Transactional
    public Diretorio addProfessor(UUID professorId, Long turmaId) {
        Profile professor = profileRepository.findById(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil do professor não encontrado"));

        if (professor.getRole() != ProfileRole.PROFESSOR) {
            throw new CustomExceptions.InvalidRoleException("O perfil deve ter a role Professor");
        }

        Diretorio diretorio = diretorioRepository.findById(turmaId)
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        diretorio.setPrimaryProfessor(professor);
        return diretorioRepository.save(diretorio);
    }

    @Transactional
    public Diretorio removeProfessor(Long turmaId) {
        Diretorio diretorio = diretorioRepository.findById(turmaId)
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        diretorio.setPrimaryProfessor(null);
        return diretorioRepository.save(diretorio);
    }

    @Transactional
    public void addAluno(UUID alunoId, Long turmaId) {
        Diretorio diretorio = diretorioRepository.findById(turmaId)
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        Profile aluno = profileRepository.findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        diretorio.addAluno(aluno);
        diretorioRepository.save(diretorio);
    }

    @Transactional
    public void addAlunos(List<UUID> alunosIds, Long turmaId) {
        Diretorio diretorio = diretorioRepository.findById(turmaId)
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        List<Profile> alunos = profileRepository.findAllById(alunosIds);

        if (alunos.size() != alunosIds.size()) {
            throw new EntityNotFoundException("Um ou mais alunos não encontrados");
        }

        alunos.forEach(diretorio::addAluno);
        diretorioRepository.save(diretorio);
    }

    @Transactional
    public void deleteDiretorio(Long diretorioId) {
        Diretorio diretorio = diretorioRepository.findById(diretorioId)
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        // Remove associação dos alunos
        diretorio.clearAlunos();

        // Remove professor
        diretorio.setPrimaryProfessor(null);

        diretorioRepository.delete(diretorio);
    }

    @Transactional
    public DiretorioDTO updateDiretorio(Long diretorioId, DiretorioUpdateDTO updateDTO) {
        Diretorio diretorio = diretorioRepository.findById(diretorioId)
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        if (updateDTO.novaTurma() != null) {
            diretorio.setTurma(updateDTO.novaTurma());
        }

        if (updateDTO.novosAlunosIds() != null) {
            // Remove alunos antigos
            diretorio.clearAlunos();

            // Adiciona novos alunos
            List<Profile> novosAlunos = profileRepository.findAllById(updateDTO.novosAlunosIds());
            if (novosAlunos.size() != updateDTO.novosAlunosIds().size()) {
                throw new EntityNotFoundException("Um ou mais alunos não encontrados");
            }
            novosAlunos.forEach(diretorio::addAluno);
        }

        if (updateDTO.novoProfessorId() != null) {
            Profile professor = profileRepository.findById(updateDTO.novoProfessorId())
                    .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));

            if (professor.getRole() != ProfileRole.PROFESSOR) {
                throw new IllegalArgumentException("O perfil deve ser um professor");
            }
            diretorio.setPrimaryProfessor(professor);
        }

        return new DiretorioDTO(diretorioRepository.save(diretorio));
    }


    @Transactional
    public void removeAlunoFromDiretorio(UUID alunoId, Long diretorioId) {
        Diretorio diretorio = diretorioRepository.findById(diretorioId)
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        Profile aluno = profileRepository.findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        diretorio.removeAluno(aluno);
        diretorioRepository.save(diretorio);
    }

}








