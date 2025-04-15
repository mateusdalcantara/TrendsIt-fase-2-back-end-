package com.trendsit.trendsit_fase2.service.diretorio;

import com.trendsit.trendsit_fase2.dto.diretorio.DiretorioDTO;
import com.trendsit.trendsit_fase2.dto.diretorio.DiretorioUpdateDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfileResponseDTO;
import com.trendsit.trendsit_fase2.exception.CustomExceptions;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.model.diretorio.Diretorio;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.diretorio.DiretorioRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DiretorioServiceImpl {
    private final DiretorioRepository diretorioRepository;

    private final ProfileRepository profileRepository;

    public DiretorioServiceImpl(DiretorioRepository diretorioRepository, ProfileRepository profileRepository) {
        this.diretorioRepository = diretorioRepository;
        this.profileRepository = profileRepository;
    }

    public List<DiretorioDTO> findAllDiretorio() {
        return diretorioRepository.findAll().stream()
                .map(d -> new DiretorioDTO(diretorioRepository.findByIdWithRelations(d.getId()).get()))
                .collect(Collectors.toList());
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

        diretorio.setProfessor(professor);
        return diretorioRepository.save(diretorio);
    }

    @Transactional
    public Diretorio removeProfessor(Long turmaId) {
        Diretorio diretorio = diretorioRepository.findById(turmaId)
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        diretorio.setProfessor(null);
        return diretorioRepository.save(diretorio);
    }

    @Transactional
    public void addAluno(UUID alunoId, Long turmaId) {
        Diretorio diretorio = diretorioRepository.findById(turmaId)
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        Profile aluno = profileRepository.findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));
        if (aluno.getRole() != ProfileRole.ALUNO){
            throw new IllegalArgumentException("Usuario nao é aluno");
        }else {
        diretorio.addAluno(aluno);
        diretorioRepository.save(diretorio);
    }}

    @Transactional
    public void deleteDiretorio(Long diretorioId) {
        Diretorio diretorio = diretorioRepository.findById(diretorioId)
                .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));

        // Remove associação dos alunos
        diretorio.clearAlunos();

        // Remove professor
        diretorio.setProfessor(null);

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
            diretorio.setProfessor(professor);
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








