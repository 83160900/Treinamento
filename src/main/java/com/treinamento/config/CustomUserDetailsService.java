package com.treinamento.config;

import com.treinamento.model.Funcionario;
import com.treinamento.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        System.out.println("[DEBUG_LOG] Loading user by CPF: " + cpf);
        Funcionario funcionario = funcionarioRepository.findByCpf(cpf)
                .orElseThrow(() -> {
                    System.out.println("[DEBUG_LOG] User not found: " + cpf);
                    return new UsernameNotFoundException("Usuário não encontrado com CPF: " + cpf);
                });

        System.out.println("[DEBUG_LOG] User found: " + funcionario.getNome() + " password: " + (funcionario.getSenha() != null ? "EXISTS" : "NULL"));
        return new User(
                funcionario.getCpf(),
                funcionario.getSenha(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + funcionario.getPerfil()))
        );
    }
}
