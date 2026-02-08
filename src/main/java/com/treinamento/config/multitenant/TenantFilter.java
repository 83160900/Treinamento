package com.treinamento.config.multitenant;

import com.treinamento.model.Funcionario;
import com.treinamento.repository.FuncionarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String cpf = authentication.getName();
            System.out.println("[DEBUG_LOG] TenantFilter: CPF found in SecurityContext: " + cpf);
            Optional<Funcionario> funcionario = funcionarioRepository.findByCpf(cpf);
            
            funcionario.ifPresent(f -> {
                System.out.println("[DEBUG_LOG] TenantFilter: CPF: " + cpf + " | Filial: " + f.getFilial() + " | Empresa: " + f.getFilial()); 
                // Nota: Atualmente usamos filial como tenant ID. 
                // Com a nova estrutura, poderíamos usar f.getCodEmpresa() para isolamento por empresa
                TenantContext.setCurrentTenant(f.getFilial());
            });
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
