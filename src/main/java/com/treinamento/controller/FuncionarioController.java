package com.treinamento.controller;

import com.treinamento.model.Funcionario;
import com.treinamento.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private com.treinamento.repository.FilialRepository filialRepository;

    @Autowired
    private com.treinamento.repository.HorarioRepository horarioRepository;

    @Autowired
    private com.treinamento.repository.EscalaRepository escalaRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Funcionario> listarTodos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String cpf = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        System.out.println("[DEBUG_LOG] Listing employees for: " + cpf + " Role: " + role);

        try {
            if (role.equals("ROLE_ADMIN")) {
                return funcionarioRepository.findAll();
            } 
            
            Optional<Funcionario> usuarioLogado = funcionarioRepository.findByCpf(cpf);
            if (usuarioLogado.isEmpty()) {
                System.out.println("[DEBUG_LOG] Logged user not found in DB: " + cpf);
                return List.of();
            }

            Funcionario logado = usuarioLogado.get();
            if (role.equals("ROLE_GESTOR")) {
                String depto = logado.getDepartamento();
                System.out.println("[DEBUG_LOG] Gestor Depto: " + depto);
                if (depto == null || depto.isEmpty()) {
                    return List.of(logado);
                }
                return funcionarioRepository.findByDepartamento(depto);
            } else if (role.equals("ROLE_COLABORADOR")) {
                return List.of(logado);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Erro ao listar funcionários: " + e.getMessage());
            e.printStackTrace();
        }
        
        return List.of();
    }

    @PostMapping
    public ResponseEntity<Funcionario> salvar(@RequestBody Funcionario funcionario) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String cpfLogado = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        System.out.println("[DEBUG_LOG] Saving employee. Logged: " + cpfLogado + " Role: " + role + " Target CPF: " + funcionario.getCpf());

        // Apenas ADMIN ou GESTOR podem cadastrar novos ou editar outros
        // GESTOR pode editar apenas funcionários do seu próprio departamento
        if (role.equals("ROLE_COLABORADOR")) {
            System.out.println("[DEBUG_LOG] Access denied for role: " + role);
            return ResponseEntity.status(403).build();
        }

        if (role.equals("ROLE_GESTOR")) {
            Optional<Funcionario> gestorOpt = funcionarioRepository.findByCpf(cpfLogado);
            if (gestorOpt.isPresent()) {
                String deptoGestor = gestorOpt.get().getDepartamento();
                // Se o funcionário sendo salvo não for do mesmo depto do gestor, nega
                if (funcionario.getDepartamento() == null || !funcionario.getDepartamento().equals(deptoGestor)) {
                    System.out.println("[DEBUG_LOG] Gestor tried to edit employee from another department");
                    return ResponseEntity.status(403).build();
                }
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        
        // Validação da Filial
        if (funcionario.getFilial() != null && !filialRepository.existsById(funcionario.getFilial())) {
            System.out.println("[DEBUG_LOG] Attempt to save employee with non-existent filial: " + funcionario.getFilial());
            return ResponseEntity.status(400).header("Error-Message", "Filial nao encontrada").build();
        }

        // Validação do Horário
        if (funcionario.getIdHorario() != null && !horarioRepository.existsById(funcionario.getIdHorario())) {
            System.out.println("[DEBUG_LOG] Attempt to save employee with non-existent horario: " + funcionario.getIdHorario());
            return ResponseEntity.status(400).header("Error-Message", "Horario nao encontrado").build();
        }

        // Validação da Escala
        if (funcionario.getIdEscala() != null && !escalaRepository.existsById(funcionario.getIdEscala())) {
            System.out.println("[DEBUG_LOG] Attempt to save employee with non-existent escala: " + funcionario.getIdEscala());
            return ResponseEntity.status(400).header("Error-Message", "Escala nao encontrada").build();
        }

        // Mantém a senha se estiver editando e não enviou senha nova
        Optional<Funcionario> existente = funcionarioRepository.findByCpf(funcionario.getCpf());
        if (existente.isPresent()) {
            Funcionario fDb = existente.get();
            if (funcionario.getSenha() == null || funcionario.getSenha().isEmpty()) {
                funcionario.setSenha(fDb.getSenha());
                funcionario.setTrocarSenha(fDb.isTrocarSenha());
            } else if (!funcionario.getSenha().equals(fDb.getSenha())) {
                // Se enviou uma senha nova (texto plano), encripta
                funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
            }
            
            // Garante que o ID (Filial + Matrícula) não mude se o CPF for o mesmo
            funcionario.setFilial(fDb.getFilial());
            funcionario.setMatricula(fDb.getMatricula());
        } else {
            // Se for um novo funcionário, configura senha inicial (3 primeiros dígitos do CPF)
            if (funcionario.getSenha() == null || funcionario.getSenha().isEmpty()) {
                if (funcionario.getCpf() != null && funcionario.getCpf().length() >= 3) {
                    String senhaInicial = funcionario.getCpf().substring(0, 3);
                    funcionario.setSenha(passwordEncoder.encode(senhaInicial));
                    funcionario.setTrocarSenha(true);
                }
            } else {
                funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
            }
        }

        return ResponseEntity.ok(funcionarioRepository.save(funcionario));
    }

    @DeleteMapping("/{filial}/{matricula}")
    public ResponseEntity<Void> excluir(@PathVariable String filial, @PathVariable String matricula) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        if (!role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        funcionarioRepository.deleteById(new com.treinamento.model.FuncionarioId(filial, matricula));
        return ResponseEntity.ok().build();
    }
}
